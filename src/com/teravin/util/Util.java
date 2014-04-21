package com.teravin.util;

public class Util {
	private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

	public static long unsigned4BytesToInt(byte[] buf, int pos) {  
        int firstByte = 0;  
        int secondByte = 0;  
        int thirdByte = 0;  
        int fourthByte = 0;  
        int index = pos;  
        firstByte = (0x000000FF & ((int) buf[index]));  
        secondByte = (0x000000FF & ((int) buf[index + 1]));  
        thirdByte = (0x000000FF & ((int) buf[index + 2]));  
        fourthByte = (0x000000FF & ((int) buf[index + 3]));  
        index = index + 4;  
        return ((long) (firstByte << 24 | secondByte << 16 | thirdByte << 8 | fourthByte)) & 0xFFFFFFFFL;  
    } 
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    
	    return data;
	}
	
	public static String toHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length);
		String sTemp;
		for (int i = 0; i < bytes.length; i++) {
			sTemp = Integer.toHexString(0xFF & bytes[i]);
			if (sTemp.length() < 2) {
				sb.append(0);
			}
			sb.append(sTemp.toUpperCase());
		}
		return sb.toString();
	}
	
	public static String convertHexToString(String hex){
		 
		  StringBuilder sb = new StringBuilder();
		  StringBuilder temp = new StringBuilder();
	 
		  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
		  for( int i=0; i<hex.length()-1; i+=2 ){
	 
		      //grab the hex in pairs
		      String output = hex.substring(i, (i + 2));
		      //convert hex to decimal
		      int decimal = Integer.parseInt(output, 16);
		      //convert the decimal to character
		      sb.append((char)decimal);
	 
		      temp.append(decimal);
		  }
	 
		  return sb.toString();
	  }
		
		public static String xorHexString(String input, String mask) {
			try {
				byte[] data = decodeHex(input.toCharArray());
				byte[] maskData = decodeHex(mask.toCharArray());
				for (int i = 0; i < data.length; i++) {
					data[i] = (byte) (data[i] ^ maskData[i]);
				}
				return new String(encodeHex(data)).toUpperCase();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		 public static char[] encodeHex(byte[] data) {
	          return encodeHex(data, true);
	     }
		 
		 public static char[] encodeHex(byte[] data, boolean toLowerCase) {
		         return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
		     }
		 
		 protected static char[] encodeHex(byte[] data, char[] toDigits) {
			         int l = data.length;
			         char[] out = new char[l << 1];
			         // two characters form the hex value.
			         for (int i = 0, j = 0; i < l; i++) {
			             out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			             out[j++] = toDigits[0x0F & data[i]];
			         }
			         return out;
			     }
		
		public static byte[] decodeHex(char[] data) {
		
	         int len = data.length;
	 
	         if ((len & 0x01) != 0) {
	             return null;
	         }
	 
	         byte[] out = new byte[len >> 1];
	 
	         // two characters form the hex value.
	         for (int i = 0, j = 0; j < len; i++) {
	             int f = toDigit(data[j], j) << 4;
	             j++;
	             f = f | toDigit(data[j], j);
	             j++;
	             out[i] = (byte) (f & 0xFF);
	         }
	 
	         return out;
	     }
		
		protected static int toDigit(char ch, int index){
	        int digit = Character.digit(ch, 16);
	        if (digit == -1) {
	           return -1;
	        }
	        return digit;
	    }
	
		public static byte[] hex2byte (byte[] b, int offset, int len) {
	        byte[] d = new byte[len];
	        for (int i=0; i<len*2; i++) {
	            int shift = i%2 == 1 ? 0 : 4;
	            d[i>>1] |= Character.digit((char) b[offset+i], 16) << shift;
	        }
	        return d;
	    }
	    /**
	     * @param s source string (with Hex representation)
	     * @return byte array
	     */
	    public static byte[] hex2byte (String s) {
	        if (s.length() % 2 == 0) {
	            return hex2byte (s.getBytes(), 0, s.length() >> 1);
	        } else {
	        	// Padding left zero to make it even size #Bug raised by tommy
	        	return hex2byte("0"+s);
	        }
	    }
}
