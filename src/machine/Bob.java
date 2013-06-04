/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import debug.Debugger;
import gui.SevenDisp;
import java.io.BufferedWriter;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.HexFile;

/**
 *
 * @author Administrator
 */
public class Bob extends Thread implements MemIoOps, NotifyOps {
    
    private final int T = 2500000 / 50;
    
    private Config cfg;
    public  Memory mem;
    private Timer tim;
    private MTimer task;
    public  Clock clk;
    private I8085 cpu;
    
    private Debugger dbg;
    
    private boolean paused;
    private boolean debug = false;
    
    private SevenDisp disp1,disp2,disp3,disp4,disp5,disp6;
    
    private int keypress;

    private enum ks {N, P1, P2, P3}
    private ks keyst = ks.N;
    
    public Bob() {
        cfg = new Config();
        mem = new Memory(this, cfg);
        tim = new Timer("Timer");
        clk = new Clock();
        cpu = new I8085(clk, this, this);
        
        dbg = new Debugger(cpu, mem);
        
        paused = true;
        
        Reset(true);
    }
    
    public void setConfig(Config c) {
        if (!cfg.equals(c)) {
            cfg = c;
            Reset(false);
        }
    }
    
    public Config getConfig() {
        return cfg;
    } 
    
    public final void Reset(boolean dirty) {
        mem.Reset(dirty);
        clk.reset();
        cpu.reset();
        keypress = 0;
        keyst = ks.N;

    }
    
    public void startEmulation() {
        if (!paused)
            return;
        
        paused = false;
        task = new MTimer(this);
        tim.scheduleAtFixedRate(task, 250, 20);
       }
    
    public void stopEmulation() {
        if (paused)
            return;
        
        paused = true;
        task.cancel();
    }
    
    public boolean isPaused() {
        return paused;
    }
    
    public void ms20() {        
        if (!paused) {

            cpu.execute(clk.getTstates()+T);
            
            switch(keyst) {
                case P1: {
                    keyst = ks.P2;
                }
                case P2: {
                    keyst = ks.P3;
                }
                case P3: {
                    keyst = ks.N;
                    keypress = 0;
                }                    
            }
        }  
    }
            
    @Override
    public void run() {
        startEmulation();
        
        boolean forever = true;
        while(forever) {
            try {
                sleep(Long.MAX_VALUE);
            } catch (InterruptedException ex) {
                Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }    

    @Override
    public int fetchOpcode(int address) {
        clk.addTstates(4);
        int opcode = mem.readByte(address) & 0xff;
        if (debug) {
            dbg.Debug(address, opcode);
        }
//        System.out.println(String.format("PC: %04X (%02X)", address,opcode));
        return opcode;
    }

    @Override
    public int peek8(int address) {
        clk.addTstates(3);
        int value = mem.readByte(address) & 0xff;
//        System.out.println(String.format("Peek: %04X,%02X (%04X)", address,value,cpu.getRegPC()));            
        return value;
    }

    @Override
    public void poke8(int address, int value) {
//        System.out.println(String.format("Poke: %04X,%02X (%04X)", address,value,cpu.getRegPC()));
        clk.addTstates(3);
        mem.writeByte(address, (byte) value);
    }

    @Override
    public int peek16(int address) {
        clk.addTstates(6);
        int lsb = mem.readByte(address) & 0xff;
        address = (address+1) & 0xffff;
        return ((mem.readByte(address) << 8) & 0xff00 | lsb);
    }

    @Override
    public void poke16(int address, int word) {
        clk.addTstates(6);
        mem.writeByte(address, (byte) word);
        address = (address+1) & 0xffff;
        mem.writeByte(address, (byte) (word >>> 8));
    }

    @Override
    public int inPort(int port) {
        clk.addTstates(4);
        port &= 0xff;
        int value = 0xff;
        switch(port) {
            case 0x0A: {
                value = keypress;
                keypress &= 0x7f;
                break;
            }
        }
//        System.out.println(String.format("In: %02X, %02X (%04X)", port,value,cpu.getRegPC()));
        return value;
    }

    @Override
    public void outPort(int port, int value) {
        clk.addTstates(4);
        port &= 0xff;
        value &= 0xff;
//        System.out.println(String.format("Out: %02X,%02X (%04X)", port,value,cpu.getRegPC()));
        switch(port) {
            case 0x0A: {
                disp1.setSegments(value);
                break;
            }
            case 0x0B: {
                disp2.setSegments(value);
                break;
            }
            case 0x0C: {
                disp3.setSegments(value);
                break;
            }
            case 0x0D: {
                disp4.setSegments(value);
                break;
            }
            case 0x0E: {
                disp5.setSegments(value);
                break;
            }
            case 0x0F: {
                disp6.setSegments(value);
                break;
            }
        }
    }

    @Override
    public boolean inSerial() {

        return false;
    }

    @Override
    public void outSerial(boolean sod) {

    }

    @Override
    public int atAddress(int address, int opcode) {
//        System.out.println(String.format("bp: %04X, %02X", address,opcode));
//        System.out.println(String.format("HL: %04X DE: %04X", cpu.getRegHL(),cpu.getRegDE()));
//        System.out.println(String.format("BC: %04X AF: %04X", cpu.getRegBC(),cpu.getRegAF()));
        dbg.Break(address, opcode);
        return opcode;
    }

    @Override
    public void execDone() {
    
    }
    
    public void setDisp1(SevenDisp disp) {
        disp1 = disp;
    }

    public void setDisp2(SevenDisp disp) {
        disp2 = disp;
    }

    public void setDisp3(SevenDisp disp) {
        disp3 = disp;
    }

    public void setDisp4(SevenDisp disp) {
        disp4 = disp;
    }

    public void setDisp5(SevenDisp disp) {
        disp5 = disp;
    }

    public void setDisp6(SevenDisp disp) {
        disp6 = disp;
    }

    public void ButtonPressed(int keycode) {
        keypress = keycode | 0x80;
        keyst = ks.P1;
    }

    public void ResetPressed() {
        Reset(false);
    }

    public void ExecPressed() {
        cpu.doInt75();
    }

    public void setDebug(boolean state) {
        debug = state;
        dbg.setVisible(state);
    }

    public void saveRam() {
        mem.saveRam();
    }
}