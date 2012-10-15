
package metablobrender1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import java.util.ArrayList;


public class RenderClass1 extends JPanel implements MouseListener, MouseMotionListener, KeyListener{
    
    Random r = new Random();
    int oldX, oldY;
    int mouseX, mouseY;
    
    ArrayList<Particle> particleAL = new ArrayList<Particle>();
    
    private final int WIDTH;
    private final int HEIGHT;
    
    private long lastTime;
    
    int clickX, clickY;
    
    boolean showTool = true;
    boolean spray = false;
    
    public RenderClass1(int W, int H){
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
        setFocusable(true); 
        requestFocusInWindow();
        
        this.WIDTH = W;
        this.HEIGHT = H;
        
        this.setBackground(Color.BLACK);
    }
    
    public void update(Graphics g){
        paint(g);
    }
    
    public void spawnParticles(int ParticleCount){
        for(int i = 0; i < ParticleCount; i++){
            
            Particle p = new Particle();
            
            int xPos = WIDTH/4 + r.nextInt(WIDTH/2);
            int yPos = HEIGHT/4 + r.nextInt(HEIGHT/2);
            
            int xVel = 0;
            int yVel = 0;
            
            int age = r.nextInt(3);
            
            p.setParticle(xPos, yPos, xVel, yVel);
            
            particleAL.add(p);
            
        }
    }
    
    private final static float InvSqrt(float x){ // line 0
        float xhalf = 0.5f * x;
        int i = Float.floatToIntBits(x); // store floating-point bits in integer
        i = 0x5f3759d5 - (i >> 1); // initial guess for Newton's method
        x = Float.intBitsToFloat(i); // convert new bits into float
        x = x*(1.5f - xhalf*x*x); // One round of Newton's method
        return x;
    }
    
    public void paint(Graphics g){
        super.paintComponent(g);
        
        float[][] pixel = new float[WIDTH][HEIGHT];
        int[][] chunk = new int[WIDTH/40][HEIGHT/40];
        
        
        float xPos, yPos, xVel, yVel;
        float ClickToX, ClickToY, ClickToP, angle;
        int width = WIDTH;
        int height = HEIGHT;
        int pullX = clickX;
        int pullY = clickY;
        g.setColor(new Color(0, 255, 50));
        
        for(int i = 0, AL = particleAL.size(); i < AL; i++){
            
            Particle p = particleAL.get(i);
            
            xPos = p.xPos;
            yPos = p.yPos;
            xVel = p.xVel;
            yVel = p.yVel;
            
            ClickToX = xPos - pullX;
            ClickToY = yPos - pullY;
            
            ClickToP = InvSqrt((float)(ClickToX*ClickToX + ClickToY*ClickToY));
            xVel += -ClickToX * ClickToP;
            yVel += -ClickToY * ClickToP;
            
            xPos += .07 * xVel;
            yPos += .07 * yVel;
            
            if(xPos > 40 && xPos < WIDTH - 40 && yPos > 40 && yPos < HEIGHT-40){
                for(int xi = -10; xi < 10; xi ++){
                    for(int yi = -10; yi < 10; yi ++){
                        
                        ClickToP = InvSqrt( (xi)*(xi) + (yi)*(yi) );
                        
                        pixel[(int) xPos + xi][(int) yPos + yi] += ClickToP;
                        
                    }
                }
                pixel[(int) xPos][(int) yPos] += .5;
                if(chunk[(int)xPos/40]  [(int)yPos/40]<= 100000){
                chunk[(int)xPos/40]  [(int)yPos/40]   += 100000;
                chunk[(int)xPos/40]  [(int)yPos/40+1] += 1;
                chunk[(int)xPos/40+1][(int)yPos/40]   += 1;
                chunk[(int)xPos/40+1][(int)yPos/40+1] += 1;
                chunk[(int)xPos/40]  [(int)yPos/40-1] += 1;
                chunk[(int)xPos/40-1][(int)yPos/40]   += 1;
                chunk[(int)xPos/40-1][(int)yPos/40-1] += 1;
                chunk[(int)xPos/40-1][(int)yPos/40+1] += 1;
                chunk[(int)xPos/40+1][(int)yPos/40-1] += 1;
                }
            }
            
            p.setParticle(xPos, yPos, xVel, yVel);
        }
        
        for(int chunkIteratorX = 0; chunkIteratorX < WIDTH/40; chunkIteratorX ++){
            for(int chunkIteratorY = 0; chunkIteratorY < HEIGHT/40; chunkIteratorY ++){
                if(chunk[chunkIteratorX][chunkIteratorY] != 0){
                    
                    int xOffSet = chunkIteratorX*40;
                    int yOffSet = chunkIteratorY*40;
                    
                    for(int ih = 0; ih < 40; ih ++){
                        for(int iw = 0; iw < 40; iw ++){
                
                            if(pixel[xOffSet + iw][yOffSet + ih] > .5){
                    
                                int red = (int)pixel[xOffSet + iw][yOffSet + ih];
                    
                                if(red > .75){
                                    red = 255;
                                }
                    
                                g.setColor( new Color(255-red, 255, 255-red));
                                g.drawRect(xOffSet+iw, yOffSet+ih, 1, 1);
                            }
                        }
                    }
                }
            }
        }
        
        //if(showTool == true){
                g.setColor(Color.WHITE);
                g.drawString("Metablobs : " + particleAL.size(), 15, 23);
                g.drawString("Framerate:" + (1000/(System.currentTimeMillis() - lastTime)), 15, 36);
                
                g.fillRect(clickX, 0, 1, 12);
                g.fillRect(0, clickY, 12, 1);
                g.fillRect(clickX, HEIGHT - 39, 1, 12); // 39 = 12 + 27
                g.fillRect(WIDTH - 18, clickY, 12, 1);  // 18 = 12 + 6
        //}
        lastTime = System.currentTimeMillis();
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        clickX = me.getX();
        clickY = me.getY();
    }

    @Override
    public void mousePressed(MouseEvent me) {}

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {
        requestFocusInWindow();
        showTool = true;
    }

    @Override
    public void mouseExited(MouseEvent me) {
        showTool = false;
    }

    @Override
    public void mouseDragged(MouseEvent me) {
        
        oldX = mouseX;
        oldY = mouseY;
        
        mouseX = me.getX();
        mouseY = me.getY();
        
        if(spray == true){ // particles inherit mouse velocity
            for(int i = 0; i < 1; i++){
            
                Particle p = new Particle();
            
                int xPos = me.getX();
                int yPos = me.getY();
                float xVel = (float)(4 * r.nextDouble()-2 + (mouseX - oldX));
                float yVel = (float)(4 * r.nextDouble()-2 + (mouseY - oldY));
                
                p.setParticle(xPos,yPos,xVel,yVel);
            
                particleAL.add(p);
            }
            
        }else{ 
            Particle p = new Particle();
                    
            int xPos = me.getX(); //+ x - 2;
            int yPos = me.getY(); //+ y - 2;
            
            float xVel = 4*r.nextFloat() - 2;
            float yVel = 4*r.nextFloat() - 2;
             
            p.setParticle(xPos, yPos, xVel, yVel);
            particleAL.add(p);
        
        }
    }

    @Override
    public void mouseMoved(MouseEvent me) {
        mouseX = me.getX();
        mouseY = me.getY();
    }

    @Override
    public void keyTyped(KeyEvent ke) {
        
        int KeyChar = ke.getKeyChar();
        
        if(KeyChar == 115 /*KeyEvent.VK_S*/){
            spray = !spray;
        }
        
    }

    @Override
    public void keyPressed(KeyEvent ke) {}

    @Override
    public void keyReleased(KeyEvent ke) {}
}