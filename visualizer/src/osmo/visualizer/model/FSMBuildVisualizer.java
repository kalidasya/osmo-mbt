package osmo.visualizer.model;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import osmo.tester.OSMOConfiguration;
import osmo.tester.generator.GenerationListener;
import osmo.tester.generator.endcondition.Length;
import osmo.tester.generator.testsuite.TestCase;
import osmo.tester.generator.testsuite.TestSuite;
import osmo.tester.model.FSM;
import osmo.tester.model.FSMTransition;

import javax.swing.JFrame;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Teemu Kanstren
 */
public class FSMBuildVisualizer extends JFrame implements GenerationListener {
  private Graph<FSMTransition, StepCounter> graph;
  private Map<String, StepCounter> edges = new HashMap<>();
  private Set<FSMTransition> vertices = new HashSet<>();
  private FSMTransition init = new FSMTransition("init");
  private FSMTransition end = new FSMTransition("end");
  private FSMTransition current = init;
  private VisualizationViewer<FSMTransition, StepCounter> vv;
  private final Layout<FSMTransition,StepCounter> layout;

  public FSMBuildVisualizer() {
    super("Model Visualizer");
    graph = new DirectedSparseMultigraph<>();
    graph.addVertex(current);
//    Layout<FSMTransition, String> layout = new CircleLayout<FSMTransition, String>(graph);
    layout = new KKLayout<>(graph);
    layout.setSize(new Dimension(800, 600)); // sets the initial size of the space
    vv = new VisualizationViewer<>(layout);
    vv.setPreferredSize(new Dimension(800, 600)); //Sets the viewing area size
    vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
    vv.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller());
    vv.getRenderer().getVertexLabelRenderer().setPosition(Position.CNTR);
    VertexLabelAsShapeRenderer<FSMTransition, StepCounter> vlasr = new VertexLabelAsShapeRenderer<>(vv.getRenderContext());
//    vv.getRenderContext().setVertexShapeTransformer(vlasr);
    vv.getRenderContext().setVertexShapeTransformer(new EllipseVertexTransformer());
//    vv.getRenderContext().setVertexLabelRenderer(new TransitionVertextLabelRenderer(Color.GREEN));
    DefaultModalGraphMouse gm = new DefaultModalGraphMouse();
    vv.addKeyListener(gm.getModeKeyListener());
    gm.setMode(ModalGraphMouse.Mode.TRANSFORMING);
    vv.setGraphMouse(gm);
    getContentPane().add(vv);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1024, 768);
    pack();
    setVisible(true);
  }

  @Override
  public void init(FSM fsm, OSMOConfiguration config) {
  }

  @Override
  public void guard(FSMTransition t) {
/*    String edge = current.getName() + "->" + t.getName();
    if (!edges.contains(edge)) {
      System.out.println("EDGE+" + edge);
      if (!vertices.contains(t)) {
        System.out.println("VERTEX+" + t);
        graph.addVertex(t);
        vertices.add(t);
      }
      edges.add(edge);
      graph.addEdge("" + index, current, t);
      index++;
      vv.repaint();
    }*/
  }

  private void addEdge(FSMTransition t) {
    String edge = current.getName() + "->" + t.getName();
    if (!edges.containsKey(edge)) {
//      System.out.println("EDGE+" + edge);
      if (!vertices.contains(t)) {
//        System.out.println("VERTEX+" + t);
        graph.addVertex(t);
        vertices.add(t);
      }
      StepCounter counter = new StepCounter();
      edges.put(edge, counter);
      graph.addEdge(counter, current, t);
      vv.repaint();
    } else {
      edges.get(edge).increment();
    }
    layout.reset();
  }
  
  @Override
  public void transition(FSMTransition t) {
    addEdge(t);
    current = t;
  }

  @Override
  public void pre(FSMTransition t) {
  }

  @Override
  public void post(FSMTransition t) {
  }

  @Override
  public void testStarted(TestCase test) {
    current = init;
  }

  @Override
  public void testEnded(TestCase test) {
    addEdge(end);
    current = end;
  }

  @Override
  public void suiteStarted(TestSuite suite) {
  }

  @Override
  public void suiteEnded(TestSuite suite) {
  }

  @Override
  public void testError(TestCase test, Exception error) {
  }
}
