package osmo.tester.parser.annotation;

import osmo.common.log.Logger;
import osmo.tester.annotation.BeforeSuite;
import osmo.tester.model.InvocationTarget;
import osmo.tester.parser.AnnotationParser;
import osmo.tester.parser.ParserParameters;

import java.lang.reflect.Method;

/**
 * Parses {@link BeforeSuite} annotations from the given model object.
 *
 * @author Teemu Kanstren
 */
public class BeforeSuiteParser implements AnnotationParser {
  private static Logger log = new Logger(BeforeSuiteParser.class);

  @Override
  public String parse(ParserParameters parameters) {
    BeforeSuite before = (BeforeSuite) parameters.getAnnotation();
    Method method = parameters.getMethod();
    parameters.getFsm().addBeforeSuite(new InvocationTarget(parameters, BeforeSuite.class));
    return "";
  }
}
