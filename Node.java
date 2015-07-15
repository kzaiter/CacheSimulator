import java.util.Arrays;

public class Node {
       public short validBit;
       public short tag;
       public int[] dataBlock = new int[16];
       public short dirty;
       public short startAddress;
       public short prevAddress;
      
       public short getprevAddress() {
   		return prevAddress;
   	}
       public void setprevAddress(short prevAddress) {
   		this.prevAddress = prevAddress;
   	}
  
       
       public short getStartAddress() {
		return startAddress;
	}

	public void setStartAddress(short startAddress) {
		this.startAddress = startAddress;
	}

	public Node() {

       }

       public Node(short validBit, short tag, short data, short dirty){

       }

       public int getValidBit() {
               return validBit;
       }
       public void setValidBit(short validBit) {
               this.validBit = validBit;
       }
       public int getTag() {
               return tag;
       }
       public void setTag(short tag) {
               this.tag = tag;
       }
 
 public int[] getDataBlock() {
		return dataBlock;
	}

	public void setDataBlock(int[] dataBlock) {
		this.dataBlock = dataBlock;
	}

	public int getDirty() {
               return dirty;
       }
       public void setDirty(short dirty) {
               this.dirty = dirty;
       }
       
       public void clearData() {
     	  Arrays.fill(this.dataBlock,0);
       }

