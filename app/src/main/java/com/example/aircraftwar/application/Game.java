package com.example.aircraftwar.application;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.MotionEvent;

import com.example.aircraftwar.MySurfaceView;

import com.example.aircraftwar.aircraft.*;
import com.example.aircraftwar.bullet.BaseBullet;
import com.example.aircraftwar.basic.AbstractFlyingObject;
import com.example.aircraftwar.prop.*;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * 游戏主面板，游戏启动
 *
 * @author ammoni
 */
public abstract class Game extends MySurfaceView {

    private int backGroundTop = 0;

    /**
     * Scheduled 线程池，用于任务调度
     */
    private final ScheduledExecutorService executorService;

    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private final int timeInterval = 20;

    private final HeroAircraft heroAircraft;
    protected final List<AbstractAircraft> enemyAircrafts;
    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;
    private final List<BaseProp> props;

    private Bitmap backgroundImage;

    public void setBackgroundImage(Bitmap backgroundImage) {
        this.backgroundImage = backgroundImage;
    }


    /**
     * 当前得分
     */
    protected int score = 0;
    protected int scoreReg = 0;

    public int getScore() {
        return score;
    }

    public void addScore(int add){
        score+=add;
    }
    /**
     * 当前时刻
     */
    private int time = 0;

    /**
     * 游戏结束标志
     */
    private boolean gameOverFlag = false;

    protected int count=0;//已生成敌机数量

    public String gameMode;
//    public boolean musicSetting;

//    protected MusicThread musicThread,bossMusicThread;

    public HeroAircraft getHeroAircraft() {
        return heroAircraft;
    }

    public List<BaseBullet> getEnemyBullets() {
        return enemyBullets;
    }

    /**
     * 游戏难度相关参数
     */
    protected int enemyMaxNumber;//屏幕中出现的敌机最大数量
    protected int mobEnemyHp,mobEnemySpeed;
    protected int eliteEnemyHp,eliteEnemySpeed;
    protected int superEliteEnemyHp,superEliteEnemySpeed;
    protected int bossEnemyHp;
    protected int heroShootCycle,enemyShootCycle;//射击周期
    protected double eliteProb;//精英机产生概率
    protected int enemyCycle;//敌机产生周期
    protected int bossThreshold;//boss机产生阈值
    protected int addDifficultyCycle=60000;//难度增加频率

    /**
     * 界面有关
     */
    static Resources resources;
    SurfaceHolder mSurfaceHolder;
    Canvas canvas;
    Paint mPaint;

    /**
     * 图片加载器
     */

    abstract public void initParameters();

    public Game(Context context) {
        super(context);
        resources = context.getResources();
        mSurfaceHolder = getmSurfaceHolder();
        mPaint = getmPaint();

        heroAircraft = HeroAircraft.getHeroAircraft();
        initParameters();//初始化参数
        enemyAircrafts = Collections.synchronizedList(new LinkedList<>());
        heroBullets = Collections.synchronizedList(new LinkedList<>());
        enemyBullets = Collections.synchronizedList(new LinkedList<>());
        props = Collections.synchronizedList(new LinkedList<>());

        /**
         * Scheduled 线程池，用于定时任务调度
         * 关于alibaba code guide：可命名的 ThreadFactory 一般需要第三方包
         * apache 第三方库： org.apache.commons.lang3.concurrent.BasicThreadFactory
         */
        this.executorService = new ScheduledThreadPoolExecutor(1,
                BasicThreadFactory.builder().namingPattern("game-action-%d").daemon(true).build());

        this.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent){
                int action = motionEvent.getActionMasked(); // 获取触摸动作类型
                float x = motionEvent.getX();               // 触摸点 X 坐标
                float y = motionEvent.getY();               // 触摸点 Y 坐标
                if(action == MotionEvent.ACTION_MOVE){
                    // 边界限制：防止飞机超出屏幕边界
                    float minX = 0;
                    float maxX = MySurfaceView.screenWidth;   // 需获取视图宽度
                    float minY = 0;
                    float maxY = MySurfaceView.screenHeight;  // 需获取视图高度

                    x = Math.min(maxX, Math.max(minX, x));
                    y = Math.min(maxY, Math.max(minY, y));
                    heroAircraft.setLocation(x,y);
                }
                return true;
            }
        });
    }

    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {
        // Todo:播放音乐

        // Scheduled 线程池，用于定时任务调度
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

        // 定时任务：绘制、对象产生、碰撞判定、击毁及结束判定
        Runnable task = () -> {

            time += timeInterval;

            // 周期性执行（控制频率）
            // 新敌机产生
            if (CycleJudge(enemyCycle)) {
                generateEnemy();
                scoreReg=score;//保存更新前的分数
            }

            // 飞机射出子弹
            if(CycleJudge(heroShootCycle)){
                heroShootAction();
            }
            if(CycleJudge(enemyShootCycle)){
                enemyShootAction();
            }

            // 子弹移动
            bulletsMoveAction();

            // 飞机移动
            aircraftsMoveAction();

            // 道具移动
            propsMoveAction();

            // 撞击检测
            crashCheckAction();

            // 后处理
            postProcessAction();

            //增加难度
            if(CycleJudge(addDifficultyCycle)){
                addDifficulty();
            }
            // 游戏结束检查英雄机是否存活
            if (heroAircraft.getHp() <= 0) {
                // 游戏结束
                executorService.shutdown();
                gameOverFlag = true;

                // Todo:Gameover音乐 & 音乐停止

                // Todo:显示排行榜

                System.out.println("Game Over!");
            }
        };

        /**
         * 以固定延迟时间进行执行
         * 本次任务执行完成后，需要延迟设定的延迟时间，才会执行新的任务
         */
        executorService.scheduleWithFixedDelay(task, timeInterval, timeInterval, TimeUnit.MILLISECONDS);

    }

    //***********************
    //      Action 各部分
    //***********************

    abstract public void generateEnemy();
    abstract public void addDifficulty();

    private boolean CycleJudge(int cycleDuration) {
        return time%cycleDuration==0;
    }

    private void enemyShootAction() {
        // TODO 敌机射击
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            synchronized (enemyBullets) {
                enemyBullets.addAll(enemyAircraft.shoot());
            }
        }
    }
    private void heroShootAction(){
        // 英雄射击
        synchronized (heroBullets) {
            heroBullets.addAll(heroAircraft.shoot());
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void propsMoveAction() {
        for (BaseProp prop : props) {
            prop.forward();
        }
    }

    /**
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction() {
        // TODO 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }

            if (heroAircraft.crash(bullet)) {
                // 英雄级撞击到精英机子弹
                // 英雄机损失一定生命值
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractAircraft enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    // Todo:击中音效

                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        // TODO 获得分数，产生道具补给
                        score += 10;
                        synchronized (props) {
                            props.addAll(enemyAircraft.drop());
                        }
                        // Todo:Boss音效
                    }
                }
                // 英雄机 与 敌机 相撞，均损毁
                if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                    enemyAircraft.vanish();
                    heroAircraft.decreaseHp(Integer.MAX_VALUE);
                }
            }
        }

        // Todo: 我方获得道具，道具生效
        for(BaseProp prop : props){
            if (heroAircraft.crash(prop)) {
                if(prop instanceof BombProp){
                    for(AbstractAircraft aircraft:enemyAircrafts){
                        ((BombProp)prop).addAircraft(aircraft);
                    }
                }
                prop.activate(this);
                prop.vanish();
            }
        }
    }

    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    private void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        props.removeIf(AbstractFlyingObject::notValid);
    }


    //***********************
    //      Paint 各部分
    //***********************

    /**
     * 重写draw方法
     * 通过重复调用draw方法，实现游戏动画
     */
    @Override
    public void draw() {
        //通过SurfaceHolder对象的lockCanvas()方法，我们可以获取当前的Canvas绘图对象
        canvas = mSurfaceHolder.lockCanvas();
        if(mSurfaceHolder == null || canvas == null){
            return;
        }

        // 绘制背景,图片滚动
        canvas.drawBitmap(backgroundImage, new Rect(0,0,backgroundImage.getWidth(),backgroundImage.getHeight()),
                new Rect(0,backGroundTop-screenHeight,screenWidth,backGroundTop),mPaint);
        canvas.drawBitmap(backgroundImage, new Rect(0,0,backgroundImage.getWidth(),backgroundImage.getHeight()),
                new Rect(0,backGroundTop,screenWidth,screenHeight+backGroundTop),mPaint);
        backGroundTop += 1;
        if (backGroundTop == screenHeight) {
            backGroundTop = 0;
        }

        // 先绘制子弹，后绘制飞机
        // 这样子弹显示在飞机的下层
        paintImageWithPositionRevised(canvas, mPaint, enemyBullets);
        paintImageWithPositionRevised(canvas, mPaint, heroBullets);

        paintImageWithPositionRevised(canvas, mPaint, enemyAircrafts);
        paintImageWithPositionRevised(canvas, mPaint, props);

        // 绘制英雄机
        canvas.drawBitmap(ImageManager.HERO_IMAGE, heroAircraft.getLocationX() - (float) heroAircraft.getWidth() / 2,
                heroAircraft.getLocationY() - (float) heroAircraft.getHeight() / 2, mPaint);

        //绘制得分和生命值
        paintScoreAndLife();

        //提交画布内容
        mSurfaceHolder.unlockCanvasAndPost(canvas);

    }

    private void paintImageWithPositionRevised(Canvas canvas, Paint mPaint, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }
        synchronized (objects) {
            for (AbstractFlyingObject object : objects) {
                Bitmap image = object.getImage();
                assert image != null : objects.getClass().getName() + " has no image! ";
                canvas.drawBitmap(image, object.getLocationX() - (float) object.getWidth() / 2,
                        object.getLocationY() - (float) object.getHeight() / 2, mPaint);
            }
        }
    }

    // Todo
    private void paintScoreAndLife() {
    }
}
