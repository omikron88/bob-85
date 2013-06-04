/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package machine;

import java.io.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.HexFile;

public final class Memory {

    public final int PAGE_SIZE = 256;
    public final int PAGE_MASK = PAGE_SIZE - 1;
    public final byte PAGE_BIT = 8;
   
    private byte[][] Ram = new byte[4][PAGE_SIZE];
    private byte[][] Rom = new byte[3][PAGE_SIZE];
    
    private byte[][] readPages = new byte[256][];
    private byte[][] writePages = new byte[256][];
    private byte[]   noMem   = new byte[PAGE_SIZE];   // simulates 0xFF no mem 
    private byte[]   fakeROM = new byte[PAGE_SIZE];   // for "write" to ROM 
    
    private Bob m;
    private Config cf;
    
    public Memory(Bob machine, Config cnf) {
        m = machine;
        cf = cnf;
    }
    
    public void Reset(boolean dirty) {
        loadRoms();
        Arrays.fill(noMem, (byte) 0xff);

        for(int n=0; n<256; n++) {
            readPages[n] = noMem;
            writePages[n] = fakeROM;
        }
        
        readPages[0] = Rom[0];
        writePages[0] = fakeROM;
        readPages[1] = Rom[1];
        writePages[1] = fakeROM;
        readPages[2] = Rom[2];
        writePages[2] = fakeROM;

        readPages[6] = Ram[0];
        writePages[6] = Ram[0];
        readPages[7] = Ram[1];
        writePages[7] = Ram[1];
        readPages[8] = Ram[2];
        writePages[8] = Ram[2];
        readPages[9] = Ram[3];
        writePages[9] = Ram[3];
    }        
      
    public byte readByte(int address) {
        return readPages[address >>> PAGE_BIT][address & PAGE_MASK];
    }
    
    public void writeByte(int address, byte value) {
        writePages[address >>> PAGE_BIT][address & PAGE_MASK] = value;
    }
        
    private void loadRoms() {
        if (!loadRomAsFile("BOB85.rom", Rom, 0, PAGE_SIZE * 3)) {
            loadRomAsResource("/roms/BOB85.rom", Rom, 0, PAGE_SIZE * 3);
        }
    }

    private boolean loadRomAsResource(String filename, byte[][] rom, int page, int size) {

        InputStream inRom = Bob.class.getResourceAsStream(filename);
        boolean res = false;

        if (inRom == null) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "RESOURCE_ROM_ERROR");
            System.out.println(String.format("%s: %s", msg, filename));
            return false;
        }

        try {
            for (int frag = 0; frag < size / PAGE_SIZE; frag++) {
                int count = 0;
                while (count != -1 && count < PAGE_SIZE) {
                    count += inRom.read(rom[page + frag], count, PAGE_SIZE - count);
                }

                if (count != PAGE_SIZE) {
                    String msg =
                        java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                        "ROM_SIZE_ERROR");
                    System.out.println(String.format("%s: %s", msg, filename));
                } else {
                    res = true;
                }
            }
        } catch (IOException ex) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "RESOURCE_ROM_ERROR");
            System.out.println(String.format("%s: %s", msg, filename));
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                inRom.close();
            } catch (IOException ex) {
                Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (res) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "ROM_RESOURCE_LOADED");
            System.out.println(String.format("%s: %s", msg, filename));
        }

        return res;
    }

    private boolean loadRomAsFile(String filename, byte[][] rom, int page, int size) {
        BufferedInputStream fIn = null;
        boolean res = false;

        try {
            try {
                fIn = new BufferedInputStream(new FileInputStream(filename));
            } catch (FileNotFoundException ex) {
                String msg =
                    java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                    "FILE_ROM_ERROR");
                System.out.println(String.format("%s: %s", msg, filename));
                //Logger.getLogger(Spectrum.class.getName()).log(Level.SEVERE, null, ex);
                return false;
            }

            for (int frag = 0; frag < size / PAGE_SIZE; frag++) {
                int count = 0;
                while (count != -1 && count < PAGE_SIZE) {
                    count += fIn.read(rom[page + frag], count, PAGE_SIZE - count);
                }

                if (count != PAGE_SIZE) {
                    String msg =
                        java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                        "ROM_SIZE_ERROR");
                    System.out.println(String.format("%s: %s", msg, filename));
                } else {
                    res = true;
                }
            }
        } catch (IOException ex) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "FILE_ROM_ERROR");
            System.out.println(String.format("%s: %s", msg, filename));
            Logger.getLogger(Bob.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (fIn != null) {
                    fIn.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        if (res) {
            String msg =
                java.util.ResourceBundle.getBundle("machine/Bundle").getString(
                "ROM_FILE_LOADED");
            System.out.println(String.format("%s: %s", msg, filename));
        }

        return res;
    }

    void saveRam() {
        try {
            HexFile hf = new HexFile(0xffff);
            hf.hexOpen("test.hex");
            hf.hexWrite(Ram[0],0,PAGE_MASK,PAGE_SIZE * 0);
            hf.hexWrite(Ram[1],0,PAGE_MASK,PAGE_SIZE * 1);
            hf.hexWrite(Ram[2],0,PAGE_MASK,PAGE_SIZE * 2);
            hf.hexWrite(Ram[3],0,PAGE_MASK,PAGE_SIZE * 3);
            hf.hexClose(0);
        } catch (IOException ex) {
            Logger.getLogger(Memory.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}