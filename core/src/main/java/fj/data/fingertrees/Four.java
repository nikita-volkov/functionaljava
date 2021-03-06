package fj.data.fingertrees;

import fj.P;
import fj.P2;
import fj.P3;
import fj.Show;
import fj.data.Option;
import fj.data.Stream;
import fj.data.vector.V4;
import fj.F;

import static fj.data.Option.none;
import static fj.data.Option.some;
import static fj.data.fingertrees.FingerTree.mkTree;

/**
 * A four-element prefix or suffix of a finger tree.
 */
public final class Four<V, A> extends Digit<V, A> {
  private final V4<A> as;

  Four(final Measured<V, A> m, final V4<A> as) {
    super(m);
    this.as = as;
  }

  public <B> B foldRight(final F<A, F<B, B>> aff, final B z) {
    return aff.f(as._1()).f(aff.f(as._2()).f(aff.f(as._3()).f(aff.f(as._4()).f(z))));
  }

  public <B> B foldLeft(final F<B, F<A, B>> bff, final B z) {
    return as.toStream().foldLeft(bff, z);
  }

  @Override public <B> B match(
      final F<One<V, A>, B> one, final F<Two<V, A>, B> two, final F<Three<V, A>, B> three,
      final F<Four<V, A>, B> four) {
    return four.f(this);
  }

  /**
   * Returns the elements of this digit as a vector.
   *
   * @return the elements of this digit as a vector.
   */
  public V4<A> values() {
    return as;
  }

  @Override P3<Option<Digit<V, A>>, A, Option<Digit<V, A>>> split1(final F<V, Boolean> predicate, final V acc) {
    final Measured<V, A> m = measured();
    final MakeTree<V, A> mk = mkTree(m);
    final F<A, V> measure = m.measure();
    final V acc1 = m.sum(acc, measure.f(as._1()));
    if (predicate.f(acc1)) {
      return P.p(none(), as._1(), some(mk.three(as._2(), as._3(), as._4())));
    } else {
      final V acc2 = m.sum(acc1, measure.f(as._2()));
      if (predicate.f(acc2)) {
        return P.p(some(mk.one(as._1())), as._2(), some(mk.two(as._3(), as._4())));
      } else if (predicate.f(m.sum(acc2, measure.f(as._3())))) {
        return P.p(some(mk.two(as._1(), as._2())), as._3(), some(mk.one(as._4())));
      } else {
        return P.p(some(mk.three(as._1(), as._2(), as._3())), as._4(), none());
      }
    }
  }

  @Override public P2<Integer, A> lookup(final F<V, Integer> o, final int i) {
    final F<A, V> m = measured().measure();
    final int s1 = o.f(m.f(as._1()));
    if (i < s1) {
      return P.p(i, as._1());
    } else {
      final int s2 = s1 + o.f(m.f(as._2()));
      if (i < s2) {
        return P.p(i - s1, as._2());
      } else {
        final int s3 = s2 + o.f(m.f(as._3()));
        if (i < s3) {
          return P.p(i - s2, as._3());
        } else {
          return P.p(i - s3, as._4());
        }
      }
    }
  }

    @Override
    public int length() {
        return 4;
    }

  public String toString() {
    return Show.digitShow(Show.<V>anyShow(), Show.<A>anyShow()).showS(this);
  }

  public Stream<A> toStream() {
    return values().toStream();
  }

}
