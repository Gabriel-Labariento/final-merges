import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class AdultCat extends Enemy{
    private static BufferedImage[] sprites;
    private enum State {PURSUE, ATTACK, CHARGING, SLASHING, JUMPING}; 
    public State currentState;
    private static final int CHARGE_DURATION = 2500;
    private static final int CHARGE_TELEGRAPH_DURATION = 500;
    private static final int JUMP_TELEGRAPH_DURATION = 3000;
    private boolean isSlashFramesDone;
    private boolean isAttackSet;
    private double chargeDirX;
    private double chargeDirY;
    private int jumpX;
    private int jumpY;
    private long chargeStartTime;
    private long jumpStartTime;
    private int initialSpeed;
    public int currentPhase;
    private static final int ATTACK_DISTANCE = (GameCanvas.TILESIZE * 3) * (GameCanvas.TILESIZE * 3);
    

    static {
        setSprites();
    }

    public AdultCat(int x, int y) {
        isBoss = true;
        lastSpriteUpdate = 0;
        lastAttackTime = 0;
        id = enemyCount++;
        identifier = NetworkProtocol.ADULTCAT;
        speed = 2;
        height = 32;
        width = 32;
        worldX = x;
        worldY = y;
        maxHealth = 60;
        hitPoints = maxHealth;
        damage = 2;
        rewardXP = 50;
        currentRoom = null;
        currSprite = 0;
        attackCDDuration = 1000;
        attackFrameDuration = 200;
        currSprite = 0;
        currentState = State.PURSUE;
        currentPhase = 1;
    }

     private static void setSprites() {
        try {
            BufferedImage left0 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/left0.png"));
            BufferedImage left1 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/left1.png"));
            BufferedImage left2 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/left2.png"));
            BufferedImage right0 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/right0.png"));
            BufferedImage right1 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/right1.png"));
            BufferedImage right2 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/right2.png"));
            BufferedImage bite0 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/bite0.png"));
            BufferedImage bite1 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/bite1.png"));
            BufferedImage bite2 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/bite2.png"));
            BufferedImage slash0 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/slash0.png"));
            BufferedImage slash1 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/slash1.png"));
            BufferedImage slash2 = ImageIO.read(AdultCat.class.getResourceAsStream("resources/Sprites/AdultCat/slash2.png"));
            sprites = new BufferedImage[] {left0, left1, left2, right0, right1, right2, bite0, bite1, bite2, slash0, slash1, slash2};

        } catch (IOException e) {
            System.out.println("Exception in Rat setSprites()" + e);
        }
    }

    @Override
    public void matchHitBoxBounds() {
        hitBoxBounds = new int[4];
        hitBoxBounds[0]= worldY;
        hitBoxBounds[1] = worldY + height;
        hitBoxBounds[2]= worldX;
        hitBoxBounds[3] = worldX + width;
    }

    @Override
    public void draw(Graphics2D g2d, int xOffset, int yOffset){
        g2d.drawImage(sprites[currSprite], xOffset, yOffset, width, height, null);
    }

    @Override
    public void updateEntity(ServerMaster gsm){
        now = System.currentTimeMillis();

        Player pursued = scanForPlayer(gsm);
        if (pursued == null) return;
        double distanceSquared = getSquaredDistanceBetween(this, pursued);

        //Phase configuration
        currentPhase = (hitPoints > (maxHealth * 0.5)) ? 1 : 2;
        
        switch (currentState) {
            case PURSUE:
                speed  = (currentPhase == 1) ? 2 : 4;
                pursuePlayer(pursued);
                updatePursuitFrame(pursued);
                if (distanceSquared <= ATTACK_DISTANCE) currentState = State.ATTACK;
                break;
            case ATTACK:
                if (now - lastAttackTime > attackCDDuration) {
                    double attackRoll = Math.random();
                    lastAttackTime = now;

                    //Values for resets
                    isAttackSet = false;
                    initialSpeed = speed;

                    //Change attack behavior depending on phase
                    if(currentPhase == 1){                    
                        if (attackRoll > 0.5) currentState = State.SLASHING;
                        else currentState = State.CHARGING;
                    }
                    else if (currentPhase == 2){
                        if (attackRoll > 0.5) currentState = State.SLASHING;
                        else if (attackRoll >  0.20) currentState = State.CHARGING;
                        else currentState = State.JUMPING;
                    }
                    
                    isAttacking = true;
                } else currSprite = 6;    
                break;
            case SLASHING:
                // Handle the slashing animation and attack in the main game loop
                updateSlashFrames();
                if (!isAttacking) {
                    createSlashAttack(gsm, pursued, null);
                    currentState = State.PURSUE;
                } 
                break;
            case CHARGING:
                chargeAtPlayer(pursued);
                if (!isAttacking) currentState = State.PURSUE;
                break;
            case JUMPING:
                jumpAtPlayer(pursued, gsm);
                if (!isAttacking) currentState = State.PURSUE;
                break;
            
            default:
                throw new AssertionError();
        }

        matchHitBoxBounds();
    }

    private void updateSlashFrames() {
        if (now - lastSpriteUpdate > attackFrameDuration) {
            //If last frame, end attack
            if (currSprite == 11) isAttacking = false;

            //Run through slash frames
            currSprite++;
            if (currSprite < 9 || currSprite > 11) currSprite = 9;
            lastSpriteUpdate = now;            
            
        }
    }

    private void updatePursuitFrame(Player pursued){
        if (now - lastSpriteUpdate > SPRITE_FRAME_DURATION) {
            if (worldX > pursued.getWorldX()) {
                currSprite++;
                if (currSprite > 2) currSprite = 0;
            } else {
                currSprite++;
                if (currSprite < 3 || currSprite > 5) currSprite = 3;
            }
            lastSpriteUpdate = now;
        }
    }

    private void createJumpFlurry(ServerMaster gsm){
        int centerX =  getCenterX();
        int centerY = getCenterY();
        int spawnRadius = GameCanvas.TILESIZE * 3;
    
        int slashCount = 3;
        
        for (int j = 0; j < slashCount; j++) {
            // generate random angle
            double angle = Math.random() * 2 * Math.PI;
            
            // generate random distance within an imaginary circle
            double distance = spawnRadius * Math.sqrt(Math.random());
            
            // Use polar coordinates to derive spawnX and spawnY
            int spawnX = (int) (centerX + distance * Math.cos(angle)) - EnemySlash.WIDTH / 2;
            int spawnY = (int) (centerY + distance * Math.sin(angle)) - EnemySlash.HEIGHT / 2;
            
            gsm.addEntity(new EnemySlash(this, spawnX, spawnY));

        }
        
    }

    private void chargeAtPlayer(Player target){
        
        if(!isAttackSet){
            int vectorX = target.getCenterX() - getCenterX();
            int vectorY = target.getCenterY() - getCenterY(); 
            double normalizedVector = Math.sqrt((vectorX*vectorX)+(vectorY*vectorY));
            //Avoids 0/0 division edge case
            if (normalizedVector == 0) normalizedVector = 1; 
            chargeDirX = vectorX / normalizedVector;
            chargeDirY = vectorY / normalizedVector;

            isAttackSet = true;
            chargeStartTime = now;

        }

        if (now  - chargeStartTime < CHARGE_TELEGRAPH_DURATION){
            //telegraph attack
            currSprite = 9;
        }
        else{
            //Stop charge attack when duration is reached
            if (now - chargeStartTime >= CHARGE_DURATION){
                isAttacking = false;
                speed = initialSpeed;
                return;
            }
            
            speed = 8;
            int newX = (int) (worldX + chargeDirX * speed);
            int newY = (int) (worldY + chargeDirY * speed);
            updatePursuitFrame(target);

            setPosition(newX, newY);
        }
    }

    private void jumpAtPlayer(Player target, ServerMaster gsm){
        if(!isAttackSet){
            isAttackSet = true;
            jumpStartTime = now;
        }

        if (now  - jumpStartTime < JUMP_TELEGRAPH_DURATION){
            //telegraph attack
            currSprite = 9;
        }
        else{
            setPosition(target.getCenterX(),  target.getCenterY());
            createJumpFlurry(gsm);
            isAttacking = false;
        }
    }
}