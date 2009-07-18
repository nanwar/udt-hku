package com.decisiontree.datagen;



/**
 *
 * SampleByteArrayConvertor
 *
 * @author Smith Tsang
 * @since 0.9
 *
 */
public class SampleByteArrayConvertor {

	public static byte[] doubleToByteArray(double d) {

		byte[] b = new byte[8];
		long l = Double.doubleToRawLongBits(d);
		for (int i = 0; i < 8; i++) {
			b[i] = new Long(l).byteValue();
			l = l >> 8;
		}
		return b;
	}


	public static double byteArrayToDouble(byte[] b) {

		long accum = 0;
		int i = 0;
		for (int shiftBy = 0; shiftBy < 64; shiftBy += 8) {
			accum |= ((long) (b[i] & 0xff)) << shiftBy;
			i++;
		}
		return Double.longBitsToDouble(accum);
	}



}
