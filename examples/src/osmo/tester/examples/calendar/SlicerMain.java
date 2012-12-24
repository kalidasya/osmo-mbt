package osmo.tester.examples.calendar;

import osmo.tester.OSMOTester;
import osmo.tester.examples.calendar.scripter.CalendarScripter;
import osmo.tester.examples.calendar.scripter.offline.OfflineScripter;
import osmo.tester.examples.calendar.testmodel.*;
import osmo.tester.generator.MainGenerator;
import osmo.tester.gui.slicing.SlicingGUI;
import osmo.tester.model.FSM;
import osmo.tester.scripting.slicing.AsciiParser;
import osmo.tester.scripting.slicing.SlicingConfiguration;

/**
 * The class used to generate tests from the calendar example.
 *
 * @author Teemu Kanstren
 */
public class SlicerMain {
  public static void main1(String[] args) {
    OSMOTester osmo = new OSMOTester();
//    osmo.addSuiteEndCondition(new Length(2));
    ModelState state = new ModelState();
//    CalendarScripter scripter = new OnlineScripter();
    CalendarScripter scripter = new OfflineScripter(state, "tests.html");
    osmo.addModelObject(state);
    osmo.addModelObject(new CalendarMeetingModel(state, scripter));
    osmo.addModelObject(new CalendarOracleModel(state, scripter));
    osmo.addModelObject(new CalendarTaskModel(state, scripter));
    osmo.addModelObject(new CalendarOverlappingModel(state, scripter));
    osmo.addModelObject(new CalendarParticipantModel(state, scripter));
    osmo.addModelObject(new CalendarErrorHandlingModel(state, scripter));
    osmo.addModelObject(new CalendarFailureModel(state, scripter));
    MainGenerator generator = osmo.initGenerator();
    //TODO: move these to SlicerMain
//    generator.initSearchableInputs();
//    generator.nextTest();
    FSM fsm = osmo.getFsm();
    SlicingGUI g = new SlicingGUI(fsm);
    g.setVisible(true);
  }

  public static void main(String[] args) throws Exception {
//    Logger.debug = true;
    AsciiParser parser = new AsciiParser();
    SlicingConfiguration conf = parser.loadAndParse("osmo-dsl.txt");
//    conf.setListener(new FSMBuildVisualizer());
    osmo.tester.scripting.slicing.SlicerMain.execute(conf);
  }

  //time limit = 10 years
  //add task, random time (DONE)
  //add event, random time (DONE)
  //add task, overlapping task (DONE)
  //add event, overlapping event (DONE)
  //add event, overlapping task (DONE)
  //remove chosen event (DONE)
  //remove events in timeframe
  //remove chosen task (DONE)
  //remove tasks in timeframe (IGNORE)
  //check tasks are always correct (post) (DONE)
  //check events are always correct (post) (DONE)
  //remove task that does not exist (DONE)
  //remove event that does not exist (DONE)
  //remove events in timeframe where none exist (IGNORE)
  //remove tasks in timeframe where none exist (IGNORE)
  //link task to several users (IGNORE)
  //link event to several users (DONE)
  //remove task from a single user while linked to others (DONE)
  //check tasks for all users (DONE)
  //check events for all users (DONE)
  //check geteventforday in post, also gettaskforday (IGNORE)
  //user boundary values for task remove and add (IGNORE)
  //create specific model object for each boundary (NO BOUNDARY PRESENT, IGNORE)
  //create more osmo.visualizer.examples of using dataflow objects (IF WE CAN THINK OF SOME)
  //create example of failing script (DONE)
  //create example of oracle in transitions (DONE)
}
