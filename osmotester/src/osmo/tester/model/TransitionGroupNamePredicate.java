package osmo.tester.model;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.functors.NullPredicate;

public class TransitionGroupNamePredicate implements Predicate {

  private TransitionName value;

  public static Predicate getInstance(TransitionName object) {
    if (object == null) {
        return NullPredicate.INSTANCE;
    }
    return new TransitionGroupNamePredicate(object);
}

  public TransitionGroupNamePredicate(TransitionName name) {
    super();
    this.value = name;
  }

  @Override
  public boolean evaluate(Object arg0) {
    return value.nameEquals((((FSMTransition) arg0).getGroup()));
  }

}
