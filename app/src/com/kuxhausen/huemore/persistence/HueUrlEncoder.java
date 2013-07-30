package com.kuxhausen.huemore.persistence;

import java.util.ArrayList;
import java.util.BitSet;

import android.util.Base64;
import android.util.Pair;

import com.kuxhausen.huemore.state.api.BulbState;

public class HueUrlEncoder {

	/**
	 * 4 bit version header.
	 * <p>
	 * 50 bit bulbs included flags.
	 * <p>
	 * 7 bit number of states.
	 * <p>
	 * STATE
	 * <p>
	 * Each state contains:
	 * <p>
	 * 9 bit properties flagging inclusion of this order of properties:
	 * <p>
	 * 1 bit on.
	 * <p>
	 * 8 bit bri.
	 * <p>
	 * 16 bit hue.
	 * <p>
	 * 8 bit sat.
	 * <p>
	 * 64 bit xy.
	 * <p>
	 * 9 bit ct.
	 * <p>
	 * 2 bit alert.
	 * <p>
	 * 4 bit effect //three more bits than needed, reserved for future.
	 * <p>
	 * 16 bit transitiontime
	 * <p>
	 */
	public HueUrlEncoder() {

	}

	public static String legacyEncode(Integer[] bulbS, BulbState[] bsRay) {
		if (bulbS != null && bsRay != null) {
			BitSet set = new BitSet();
			int index = 0;// points to the next spot
			/** Set 4 bit protocol version **/
			{
				set.set(index, false);
				index++;
				set.set(index, false);
				index++;
				set.set(index, false);
				index++;
				set.set(index, true);
				index++;
			}
			/** Set 50 bit bulbs flags **/
			{
				set.set(index, index + 50, false);
				for (int bulb : bulbS)
					set.set(index + (bulb - 1), true);
				index += 50;
			}
			/** Set num states **/
			{
				int stateLength = 0; // number of non null bulb states
				for (BulbState bs : bsRay) {
					if (bs != null)
						stateLength++;
				}

				int bitMask = 1;
				for (int i = 0; i < 7; i++) {
					if ((stateLength & bitMask) > 0) {
						set.set(index, true);
						index++;
					} else {
						set.set(index, false);
						index++;
					}
					bitMask *= 2;
				}
			}
			/** Encode each state **/
			{
				for (BulbState bs : bsRay) {
					/** Put 9 bit properties flags **/
					{
						// On/OFF flag always include in v1 implementation 1
						set.set(index, true);
						index++;

						// Put bri flag
						if (bs.bri != null)
							set.set(index, true);
						index++;

						// Put hue flag
						if (bs.hue != null)
							set.set(index, true);
						index++;

						// Put sat flag
						if (bs.sat != null)
							set.set(index, true);
						index++;

						// Put xy flag
						if (bs.xy != null)
							set.set(index, true);
						index++;

						// Put ct flag
						if (bs.ct != null)
							set.set(index, true);
						index++;

						// Put alert flag
						if (bs.alert != null)
							set.set(index, true);
						index++;

						// Put effect flag
						if (bs.effect != null)
							set.set(index, true);
						index++;

						// Put transitiontime flag
						if (bs.transitiontime != null)
							set.set(index, true);
						index++;

					}
					/** Put on bit **/
					{
						// On/OFF flag always include in v1 implementation 1
						set.set(index, bs.on);
						index++;
					}
					/** Put 8 bit bri **/
					{
						if (bs.bri != null) {
							int bitMask = 1;
							for (int i = 0; i < 8; i++) {
								if ((bs.bri & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}
					/** Put 16 bit hue **/
					{
						if (bs.hue != null) {
							int bitMask = 1;
							for (int i = 0; i < 16; i++) {
								if ((bs.hue & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}

					/** Put 8 bit sat **/
					{
						if (bs.sat != null) {
							int bitMask = 1;
							for (int i = 0; i < 8; i++) {
								if ((bs.sat & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}
					/** Put 64 bit xy **/
					{
						// TODO implement xy mode
						if (bs.xy != null) {
							int x = Float
									.floatToIntBits((float) ((double) bs.xy[0]));

							int bitMask = 1;
							for (int i = 0; i < 32; i++) {
								if ((x & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}

							int y = Float
									.floatToIntBits((float) ((double) bs.xy[1]));

							bitMask = 1;
							for (int i = 0; i < 32; i++) {
								if ((y & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}
					/** Put 9 bit ct **/
					{
						if (bs.ct != null) {
							int bitMask = 1;
							for (int i = 0; i < 9; i++) {
								if ((bs.ct & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}
					/** Put 2 bit alert **/
					{
						if (bs.alert != null) {
							int value = 0;
							if (bs.alert.equals("none"))
								value = 0;
							else if (bs.alert.equals("select"))
								value = 1;
							else if (bs.alert.equals("lselect"))
								value = 2;

							int bitMask = 1;
							for (int i = 0; i < 2; i++) {
								if ((value & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}
					/** Put 4 bit effect **/
					{
						// three more bits than needed, reserved for future API
						// functionality
						if (bs.effect != null) {
							int value = 0;
							if (bs.effect.equals("none"))
								value = 0;
							else if (bs.effect.equals("colorloop"))
								value = 1;

							int bitMask = 1;
							for (int i = 0; i < 4; i++) {
								if ((value & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}
					/** Put 16 bit transitiontime **/
					{
						if (bs.transitiontime != null) {
							int bitMask = 1;
							for (int i = 0; i < 16; i++) {
								if ((bs.transitiontime & bitMask) > 0) {
									set.set(index, true);
									index++;
								} else {
									set.set(index, false);
									index++;
								}
								bitMask *= 2;
							}
						}
					}
				}
			}

			byte[] intermediaryResult = fromBitSet(set, index);
			return Base64.encodeToString(intermediaryResult, Base64.URL_SAFE);
		}

		return "";
	}

	public static Pair<Integer[], BulbState[]> legacyDecode(String encoded) {
		ArrayList<Integer> bList = null;
		BulbState[] bsRay = null;
		try {
			byte[] intermediaryReverse = Base64
					.decode(encoded, Base64.URL_SAFE);
			BitSet set = toBitSet(intermediaryReverse);
			bList = new ArrayList<Integer>();

			int index = 0;// points to the next spot
			/** Get protocol version **/
			{
				// Make sure the protocol is version 1 (0001)
				if (set.get(0) || set.get(1) || set.get(2) || !set.get(3)) {
					// Unsupported protocol version
					return new Pair(null, null);
				}
				index += 4;
			}
			/** Set bulbs flags version **/
			{

				for (int i = 0; i < 50; i++)
					if (set.get(index + i))
						bList.add(i + 1);
				index += 50;
			}
			/** Set num states **/
			{
				int numStates = 0;
				int bitMask = 1;
				for (int i = 0; i < 7; i++) {
					if (set.get(index))
						numStates |= bitMask;
					index++;
					bitMask *= 2;
				}
				bsRay = new BulbState[numStates];
			}

			/** Decode each state **/
			{
				for (int i = 0; i < bsRay.length; i++) {

					/**
					 * On, Bri, Hue, Sat, XY, CT, Alert, Effect, Transitiontime
					 */
					boolean[] propertiesFlags = new boolean[9];
					BulbState bs = new BulbState();
					/** Get 9 bit properties flags **/
					{
						for (int j = 0; j < 9; j++) {
							propertiesFlags[j] = set.get(index);
							index++;
						}

					}
					/** Get on bit **/
					{
						bs.on = set.get(index);
						index++;
					}
					/** Get 8 bit bri **/
					{
						if (propertiesFlags[1]) {
							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 8; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							bs.bri = value;
						}
					}
					/** Get 16 bit hue **/
					{
						if (propertiesFlags[2]) {
							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 16; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							bs.hue = value;
						}
					}

					/** Get 8 bit sat **/
					{
						if (propertiesFlags[3]) {
							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 8; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							bs.sat = (short) value;
						}
					}
					/** Get 64 bit xy **/
					{
						if (propertiesFlags[4]) {

							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 32; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							Double x = (double) Float.intBitsToFloat(value);
							value = 0;
							bitMask = 1;
							for (int j = 0; j < 32; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							Double y = (double) Float.intBitsToFloat(value);
							bs.xy = new Double[] { x, y };
						}
					}
					/** Get 9 bit ct **/
					{
						if (propertiesFlags[5]) {
							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 9; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							bs.ct = value;
						}
					}
					/** Get 2 bit alert **/
					{
						if (propertiesFlags[6]) {
							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 2; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							switch (value) {
							case 0:
								bs.alert = "none";
								break;
							case 1:
								bs.alert = "select";
								break;
							case 2:
								bs.alert = "lselect";
								break;
							}
						}
					}
					/** Get 4 bit effect **/
					{
						// three more bits than needed, reserved for future API
						// functionality
						if (propertiesFlags[7]) {
							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 4; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							switch (value) {
							case 0:
								bs.effect = "none";
								break;
							case 1:
								bs.effect = "colorloop";
								break;
							}
						}
					}
					/** Get 16 bit transitiontime **/
					{
						if (propertiesFlags[8]) {
							int value = 0;
							int bitMask = 1;
							for (int j = 0; j < 16; j++) {
								if (set.get(index))
									value |= bitMask;
								index++;
								bitMask *= 2;
							}
							bs.transitiontime = value;
						}
					}
					bsRay[i] = bs;
				}
			}

		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new Pair<Integer[], BulbState[]>(bList.toArray(new Integer[bList
				.size()]), bsRay);
	}

	public static byte[] fromBitSet(BitSet bits, int length) {
		while (length % 8 != 0) {
			length++;
		}

		byte[] bytes = new byte[length / 8];
		for (int i = 0; i < bytes.length; i++) {
			byte mask = 1;
			byte temp = 0;
			for (int j = 0; j < 8; j++) {
				if (bits.get(8 * i + j))
					temp |= mask;
				mask = (byte) (mask << 1);
			}
			bytes[i] = (temp);
		}

		return bytes;
	}

	public static BitSet toBitSet(byte[] bytes) {
		BitSet bits = new BitSet();
		for (int i = 0; i < bytes.length; i++) {
			byte mask = 1;
			byte temp = bytes[i];
			for (int j = 0; j < 8; j++) {
				if ((temp & mask) != 0)
					bits.set(8 * i + j, true);
				mask = (byte) (mask << 1);
			}
		}
		return bits;
	}
}