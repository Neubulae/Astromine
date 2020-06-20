package com.github.chainmailstudios.astromine.common.fraction;

import net.minecraft.nbt.CompoundTag;

import com.google.common.base.Objects;
import com.google.common.math.LongMath;
import java.math.RoundingMode;

public class Fraction extends Number implements Comparable<Fraction> {
	public static final Fraction EMPTY = new Fraction(0, 1);
	public static final Fraction BUCKET = new Fraction(1, 1);
	public static final Fraction BOTTLE = new Fraction(1, 3);
	private long numerator;
	private long denominator;

	public Fraction(long numerator, long denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public static Fraction add(Fraction fractionA, Fraction fractionB) {
		long denominator = lowestCommonDenominator(fractionA.denominator, fractionB.denominator);

		return new Fraction(fractionA.numerator * (denominator / fractionA.denominator) + fractionB.numerator * (denominator / fractionB.denominator), denominator);
	}

	private static long lowestCommonDenominator(long a, long b) {
		return a == b ? a : a == 1 ? b : b == 1 ? a : (a * b) /  greatestCommonDivisor(a, b);
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

	public static Fraction subtract(Fraction fractionA, Fraction fractionB) {
		long denominator = lowestCommonDenominator(fractionA.denominator, fractionB.denominator);

		return new Fraction(fractionA.numerator * (denominator / fractionA.denominator) - fractionB.numerator * (denominator / fractionB.denominator), denominator);
	}

	public void subtract(Fraction fraction) {
		Fraction result = Fraction.subtract(this, fraction);
		this.numerator = result.numerator;
		this.denominator = result.denominator;
	}

	public static Fraction divide(Fraction fractionA, Fraction fractionB) {
		return multiply(fractionA, Fraction.inverse(fractionB));
	}

	public void divide(Fraction fraction) {
		Fraction result = Fraction.divide(this, fraction);
		this.numerator = result.numerator;
		this.denominator = result.denominator;
	}

	public static Fraction multiply(Fraction fractionA, Fraction fractionB) {
		return new Fraction(fractionA.numerator * fractionB.numerator, fractionA.denominator * fractionB.denominator);
	}

	public void multiply(Fraction fraction) {
		Fraction result = Fraction.multiply(this, fraction);
		this.numerator = result.numerator;
		this.denominator = result.denominator;
	}

	/**
	 * Fraction inversion method.
	 */
	public static Fraction inverse(Fraction fraction) {
		return new Fraction(fraction.denominator, fraction.numerator);
	}

	public void inverse() {
		Fraction result = Fraction.inverse(this);
		this.numerator = result.numerator;
		this.denominator = result.denominator;
	}

	public static Fraction limit(Fraction source, Fraction target) {
		return new Fraction(source.numerator * (target.denominator / source.denominator), target.denominator);
	}

	public void limit(Fraction fraction) {
		Fraction result = Fraction.limit(this, fraction);
		this.numerator = result.numerator;
		this.denominator = result.denominator;
	}

	public static Fraction min(Fraction fractionA, Fraction fractionB) {
		return (fractionA.isSmallerThan(fractionB) ? fractionA : fractionB);
	}

	public boolean isSmallerThan(Fraction fraction) {
		return !this.isBiggerThan(fraction);
	}

	public boolean isBiggerThan(Fraction fraction) {
		if (this.denominator == fraction.denominator) {
			return this.numerator > fraction.numerator;
		}

		long denominator = lowestCommonDenominator(this.denominator, fraction.denominator);

		return this.numerator * (this.denominator * (denominator / this.denominator)) > fraction.numerator * (fraction.denominator * (denominator / fraction.denominator));
	}

	/**
	 * Fraction comparison method.
	 */
	public boolean isSmallerOrEqualThan(Fraction fraction) {
		return isSmallerThan(fraction) || equals(fraction);
	}

	/**
	 * Fraction comparison method.
	 */
	public boolean isBiggerOrEqualThan(Fraction fraction) {
		return isBiggerThan(fraction) || equals(fraction);
	}

	/**
	 * Fraction comparison method.
	 */
	public static Fraction max(Fraction fractionA, Fraction fractionB) {
		return (fractionA.isBiggerThan(fractionB) ? fractionA : fractionB);
	}

	public static Fraction fromTag(CompoundTag tag) {
		long[] values = tag.getLongArray("values");

		return new Fraction(values[0], values[1]);
	}

	public CompoundTag toTag(CompoundTag tag) {
		tag.putLongArray("values", new long[] {this.numerator, this.denominator});

		return tag;
	}

	public long getNumerator() {
		return this.numerator;
	}

	public void setNumerator(long numerator) {
		this.numerator = numerator;
	}

	public long getDenominator() {
		return this.denominator;
	}

	public void setDenominator(long denominator) {
		this.denominator = denominator;
	}

	@Override
	public int compareTo(Fraction fraction) {
		return this.isBiggerThan(fraction) ? 1 : this.isSmallerThan(fraction) ? -1 : 0;
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

	public static Fraction simplify(Fraction fraction) {
		if (fraction.numerator == 0) {
			return Fraction.EMPTY;
		}

		long divisor = greatestCommonDivisor(fraction.numerator, fraction.denominator);

		return new Fraction(fraction.numerator / divisor, fraction.denominator / divisor);
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
		return (float) this.longValue();
	}

	@Override
	public double doubleValue() {
		return (double) this.longValue();
	}
}
