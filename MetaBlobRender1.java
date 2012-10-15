
package metablobrender1;

import javax.swing.JFrame;

public class MetaBlobRender1 {
    
    public static final int WIDTH= 1200;
    public static final int HEIGHT = 1200;
    public static final int PARTICLECOUNT = 1;
    public static final int TICK = 33;
    
    public static void main(String[] args) {
        
        JFrame frame = new JFrame("ParticleMaker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(WIDTH, HEIGHT);
        frame.setResizable(false);
        frame.setFocusable(true);
        
        final RenderClass1 ren = new RenderClass1(WIDTH, HEIGHT);
        ren.spawnParticles(PARTICLECOUNT);
        frame.add(ren);
        
        frame.setVisible(true); // NEEDS TO BE AFTER ADDING ALL OBJECTS TO FRAME
        
        final boolean stop = false; // final and yet needs to not be final to be useful
        
        Thread runThread = new Thread(new Runnable(){
            public void run(){
              if(stop != true){
                  for(int i = 0; i < 1000000; i++){ //not infinite, stops after 1000000 repaint()'s
                      ren.repaint( );
                      try{Thread.sleep(TICK);}catch(Exception e){System.out.println("Exception e at Thread.sleep");}
                  }
              }
          }
        });
        
        runThread.start();
        
    }
}