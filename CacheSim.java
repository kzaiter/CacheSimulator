import java.util.Arrays;
import java.util.Scanner;

public class cacheSim {

	public static String input;
	public static short address;
	public static short startAddress;
	public static short offset;
	public static short tag;
	public static short slot;

	public static Node[] cache = new Node[16];
	public static int cacheSize = 16;
	public static int memSize = 2048;
	public static int[] main_Mem = new int[memSize];

	static Scanner keyboard = new Scanner(System.in);

	public static void menu() {


		System.out.println("[r] to read   [w] to write   [d] to display");
		input = keyboard.next();
		if (input.equals("r")) {
			readAddress();
		}
		else if (input.equals("w")) {
			writeAddress();
		}
		else if (input.equals("d")) {
			display();
		}
		else if (input.equals("p")) {

			System.out.println(Arrays.toString(main_Mem));
			for(int i=0; i<16; i++){
				System.out.println(Arrays.toString(cache[slot].dataBlock));
				menu();
			}
		}
		else {
			System.out.println("Bad input try again");
			menu();
		}
	}

	public static void readAddress() {

		System.out.println("What address would you like read? ");
		address = keyboard.nextShort(16);

		startAddress = (short) (address & 0xfff0);
		tag = (short) ((address >> 8) );
		slot = (short) ((address & 0x00f0) >> 4);

		//Valid bit is 0, empty slot--MISS
		if (cache[slot].getValidBit() == 0) {       	    
			cache[slot].setValidBit((short) 1);
			cache[slot].setTag(tag);
			cache[slot].setStartAddress(startAddress);

			// main to cache
			System.arraycopy(main_Mem, cache[slot].getStartAddress(), cache[slot].dataBlock, 0, cacheSize);
			System.out.println("Cache Miss");
		}
		//Valid bit 1 but tags don't match--MISS
		else if (cache[slot].getValidBit() == 1 && cache[slot].getTag() != tag) {
			System.out.println("Cache Miss");
			
			if (cache[slot].getDirty() == 1) {	
				//copy contents of slot back into main memory before loading new block
				System.arraycopy(cache[slot].dataBlock, 0, main_Mem,cache[slot].getStartAddress(), cacheSize);
				cache[slot].setDirty((short) 0);
			}
			//set up new block
			startAddress = (short) (address & 0xfff0);
			cache[slot].setTag(tag);
			cache[slot].setStartAddress(startAddress);
			System.arraycopy(main_Mem, cache[slot].getStartAddress(), cache[slot].dataBlock, 0, cacheSize);

		}

		//Valid bit 1 and tags match, hit
		else if (cache[slot].getValidBit() == 1 && tag == cache[slot].getTag()) {
			System.out.println("Cache Hit");
			System.out.print("The value at address ");
            System.out.printf("%X", 0xffFF & address);
            System.out.print(" is: ");
            System.out.println(Integer.toHexString(main_Mem[address]));
			System.out.println();
		}

		menu();
	}

	public static void writeAddress() {

		System.out.println("What address do you want to write to? ");
		address = keyboard.nextShort(16);
		System.out.println("And what value do you want to write to it? ");
		int input = keyboard.nextInt(16);
		startAddress = (short) (address & 0xfff0);
		tag = (short) ((address >> 8) );
		slot = (short) ((address & 0x00f0) >> 4);

		//Valid bit 1, tag matches, hit, just modify value
		if (cache[slot].getValidBit() != 0 && cache[slot].getTag() == tag) {
			System.out.println("Cache Hit");
			for (int i = 0; i <16; i++) {
				if (i == (0x000F & address)) {
					//System.out.println(address);

					cache[slot].dataBlock[i] = input;
					cache[slot].setDirty((short) 1);

				}
			}
		}
		//Valid bit 1, tags don't match-Check dirty bit and write back first if valid
		else if (cache[slot].getValidBit() == 1 && cache[slot].getTag() != tag) {
			if (cache[slot].getDirty() ==1) {
				//copy from cache to main
				System.arraycopy(cache[slot].dataBlock, 0, main_Mem,cache[slot].getStartAddress(), cacheSize);
			}

			System.out.println("Cache Miss");
			cache[slot].setValidBit((short) 1);
			cache[slot].setTag(tag);
			cache[slot].setDirty((short) 1);
			cache[slot].setStartAddress(startAddress);

			//copy from main to cache
			System.arraycopy(main_Mem, cache[slot].getStartAddress(), cache[slot].dataBlock, 0, cacheSize);


			for (int i = 0; i <16; i++) {
				if (i == (0x000f & address)) {
					cache[slot].dataBlock[i] = input;	
				}
			}
		}

		//Empty slot, no need to write back
		else if (cache[slot].getValidBit() == 0) {
			System.out.println("Cache Miss");

			cache[slot].setValidBit((short) 1);
			cache[slot].setTag(tag);
			cache[slot].setDirty((short) 1);
			cache[slot].setStartAddress(startAddress);
			main_Mem[address]=input;
			//copy from main to cache
			System.arraycopy(main_Mem, cache[slot].getStartAddress(), cache[slot].dataBlock, 0, cacheSize);

			//writes to value to cache
			for (short i = 0; i <=15; i++) {
				if (cache[slot].dataBlock[i] == (0xFF & address)) {
					cache[slot].dataBlock[i] = input;
					cache[slot].setDirty((short) 1);
				}
			}
		}

		menu();
	}

	public static void display() {

		System.out.println("Slot  Valid  Tag     Data");
		for (int i = 0; i < 16; i++) {
			System.out.print(Integer.toHexString(i));
			System.out.print("     ");
			System.out.print(cache[i].getValidBit());
			System.out.print("      ");
			System.out.printf("%X", cache[i].getTag());
			System.out.print("       ");
			for (int j = 0; j <cacheSize; j++) {
				System.out.printf("%X", cache[i].dataBlock[j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		menu();
	}
	public static void main(String[] args) {

		//initialize main memory
		for(short i=0x0;i<=0x7ff;i++){
			main_Mem[i]= (short) (i & 0x0ff);  
		}
		//initialize cache slots to 0
		for (int i = 0; i<cache.length; i++) {
			cache[i] = new Node();
			cache[i].setValidBit((short) 0);
			cache[i].setTag((short) 0);
			cache[i].setDirty((short) 0);
		}
		//clears array to 0 that is block of data
		for (int i = 0; i<cache.length; i++) {
			cache[i].clearData();
		}
		
		menu();
	}
}
