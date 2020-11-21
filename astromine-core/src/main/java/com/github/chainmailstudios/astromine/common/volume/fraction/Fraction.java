/*
 * MIT License
 *
 * Copyright (c) 2020 Chainmail Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.chainmailstudios.astromine.common.volume.fraction;

import net.minecraft.nbt.CompoundTag;

import com.google.common.base.Objects;
import java.text.DecimalFormat;

public final class Fraction extends Number implements Comparable<Fraction> {
	public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("###.###");

	public static final Fraction MINIMUM = new Fraction(1, Long.MAX_VALUE);
	public static final Fraction MAXIMUM = new Fraction(Long.MAX_VALUE, 1);

	public static final Fraction EMPTY = new Fraction(0, 1);
	public static final Fraction BUCKET = new Fraction(1, 1);
	public static final Fraction BOTTLE = new Fraction(1, 3);

	public static final Fraction INGOT = new Fraction(1, 9);
	public static final Fraction NUGGET = new Fraction(1, 81);

	private final long numerator;
	private final long denominator;

	public Fraction(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = Math.max(1, denominator);
	}

	public static Fraction of(long whole) {
		return new Fraction(whole, 1);
	}

	public static Fraction of(long numerator, long denominator) {
		return new Fraction(numerator, denominator);
	}

	public static Fraction of(long whole, long numerator, long denominator) {
		return new Fraction(numerator + whole * denominator, denominator);
	}

	public static Fraction ofDecimal(double d) {
		String s = Fraction.DECIMAL_FORMAT.format(d);
		int digitsDec = s.length() - 1 - s.indexOf('.');

		int denom = 1;
		for (int i = 0; i < digitsDec; i++) {
			d *= 10;
			denom *= 10;
		}
		int num = (int) Math.round(d);

		return Fraction.simplify(Fraction.of(num, denom));
	}

	public static Fraction add(Fraction fractionA, Fraction fractionB) {
		long denominator = lowestCommonDenominator(fractionA.denominator, fractionB.denominator);

		return new Fraction(fractionA.numerator * (denominator / fractionA.denominator) + fractionB.numerator * (denominator / fractionB.denominator), denominator);
	}

	public static Fraction subtract(Fraction fractionA, Fraction fractionB) {
		long denominator = lowestCommonDenominator(fractionA.denominator, fractionB.denominator);

		return new Fraction(fractionA.numerator * (denominator / fractionA.denominator) - fractionB.numerator * (denominator / fractionB.denominator), denominator);
	}

	public static Fraction divide(Fraction fractionA, Fraction fractionB) {
		return multiply(fractionA, Fraction.inverse(fractionB));
	}

	public static Fraction multiply(Fraction fractionA, Fraction fractionB) {
		return new Fraction(fractionA.numerator * fractionB.numerator, fractionA.denominator * fractionB.denominator);
	}

	public static Fraction inverse(Fraction fraction) {
		return new Fraction(fraction.denominator, fraction.numerator);
	}

	public static Fraction limit(Fraction source, Fraction target) {
		return new Fraction(source.numerator * (target.denominator / source.denominator), target.denominator);
	}

	public static Fraction minimum(Fraction fractionA, Fraction fractionB) {
		return (fractionA.smallerThan(fractionB) ? fractionA : fractionB);
	}

	public static Fraction maximum(Fraction fractionA, Fraction fractionB) {
		return (fractionA.biggerThan(fractionB) ? fractionA : fractionB);
	}

	public static Fraction simplify(Fraction fraction) {
		if (fraction.numerator == 0)
			return new Fraction(0, 1);
		if ((fraction.numerator <= 0 && fraction.denominator >= 0) || (fraction.numerator >= 0 && fraction.denominator <= 0)) {
			return fraction;
		}

		long divisor = greatestCommonDivisor(fraction.numerator, fraction.denominator);

		return new Fraction(fraction.numerator / divisor, fraction.denominator / divisor);
	}

	public static Fraction fromTag(CompoundTag tag) {
		long[] values = tag.getLongArray("values");

		if (values.length != 2)
			values = new long[]{ 0, 0 };

		return new Fraction(values[0], values[1]);
	}

	private static long lowestCommonDenominator(long a, long b) {
		return a == b ? a : a == 1 ? b : b == 1 ? a : (a * b) / greatestCommonDivisor(a, b);
	}

	/**
	 * Iterative version of Stein's Algorithm for greatest common divisor.
	 */
	private static long greatestCommonDivisor(long a, long b) {
		long shift = 0;

		if (a == 0) {
			return b;
		}
		if (b == 0) {
			return a;
		}

		while (((a | b) & 1) == 0) {
			++shift;
			a >>= 1;
			b >>= 1;
		}

		while ((a & 1) == 0) {
			a >>= 1;
		}

		do {
			while ((b & 1) == 0) {
				b >>= 1;
			}
			if (a > b) {
				long t = b;
				b = a;
				a = t;
			}

			b -= a;
		} while (b != 0);

		return a << shift;
	}

	public long getNumerator() {
		return this.numerator;
	}

	public long getDenominator() {
		return this.denominator;
	}

	public Fraction add(Fraction fractionB) {
		return Fraction.add(this, fractionB);
	}

	public Fraction subtract(Fraction fractionB) {
		return Fraction.subtract(this, fractionB);
	}

	public Fraction divide(Fraction fractionB) {
		return Fraction.divide(this, fractionB);
	}

	public Fraction multiply(Fraction fractionB) {
		return Fraction.multiply(this, fractionB);
	}

	public Fraction inverse() {
		return Fraction.inverse(this);
	}

	public Fraction limit(Fraction target) {
		return Fraction.limit(this, target);
	}

	public Fraction minimum(Fraction fractionB) {
		return Fraction.minimum(this, fractionB);
	}

	public Fraction maximum(Fraction fractionB) {
		return Fraction.maximum(this, fractionB);
	}

	public Fraction simplify() {
		return Fraction.simplify(this);
	}

	public boolean smallerThan(Fraction fraction) {
		return !this.biggerThan(fraction);
	}

	public Fraction ifSmallerThan(Fraction fraction, Runnable runnable) {
		if (smallerThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public Fraction ifNotSmallerThan(Fraction fraction, Runnable runnable) {
		if (!smallerThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public boolean biggerThan(Fraction fraction) {
		if (this.denominator == fraction.denominator) {
			return this.numerator > fraction.numerator;
		}

		long denominator = lowestCommonDenominator(this.denominator, fraction.denominator);

		Fraction fA = new Fraction(this.numerator * (this.denominator * (denominator / this.denominator)), this.denominator);
		Fraction fB = new Fraction(fraction.numerator * (fraction.denominator * (denominator / fraction.denominator)), fraction.denominator);

		return fA.longValue() > fB.longValue();
	}

	public Fraction ifBiggerThan(Fraction fraction, Runnable runnable) {
		if (biggerThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public Fraction ifNotBiggerThan(Fraction fraction, Runnable runnable) {
		if (!biggerThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public boolean smallerOrEqualThan(Fraction fraction) {
		return smallerThan(fraction) || equals(fraction);
	}

	public Fraction ifSmallerOrEqualThan(Fraction fraction, Runnable runnable) {
		if (smallerOrEqualThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public Fraction ifNotSmallerOrEqualThan(Fraction fraction, Runnable runnable) {
		if (!smallerOrEqualThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public boolean biggerOrEqualThan(Fraction fraction) {
		return biggerThan(fraction) || equals(fraction);
	}

	public Fraction ifBiggerOrEqualThan(Fraction fraction, Runnable runnable) {
		if (biggerOrEqualThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public Fraction ifNotBiggerOrEqualThan(Fraction fraction, Runnable runnable) {
		if (!biggerOrEqualThan(fraction)) {
			runnable.run();
		}

		return this;
	}

	public CompoundTag toTag() {
		CompoundTag tag = new CompoundTag();

		tag.putLongArray("values", new long[]{ this.numerator, this.denominator });

		return tag;
	}

	@Override
	public int compareTo(Fraction fraction) {
		return this.biggerThan(fraction) ? 1 : this.smallerThan(fraction) ? -1 : 0;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.numerator, this.denominator);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (!(object instanceof Fraction)) {
			return false;
		}

		Fraction target = (Fraction) object;

		Fraction simplifiedA = Fraction.simplify(this);
		Fraction simplifiedB = Fraction.simplify(target);

		return simplifiedA.numerator == simplifiedB.numerator && simplifiedA.denominator == simplifiedB.denominator;
	}

	public Fraction copy() {
		return new Fraction(this.numerator, this.denominator);
	}

	public String toFractionalString() {
		return numerator + ":" + denominator;
	}

	public String toDecimalString() {
		return DECIMAL_FORMAT.format(floatValue());
	}

	@Override
	public String toString() {
		return "Fraction{" + "numerator=" + this.numerator + ", denominator=" + this.denominator + '}';
	}

	@Override
	public int intValue() {
		return (int) this.longValue();
	}

	@Override
	public long longValue() {
		return this.numerator / this.denominator;
	}

	@Override
	public float floatValue() {
		return (float) this.numerator / (float) this.denominator;
	}

	@Override
	public double doubleValue() {
		return (double) this.numerator / (double) this.denominator;
	}
}
