package org.spring.mr.pagerank;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class Reduce
    extends Reducer<Text, Text, Text, Text> {

  public static final double CONVERGENCE_SCALING_FACTOR = 1000.0;
  //0.85的系数
  public static final double DAMPING_FACTOR = 0.85;
  public static String CONF_NUM_NODES_GRAPH = "pagerank.numnodes";
  private int numberOfNodesInGraph;

  public static enum Counter {
    CONV_DELTAS
  }

  @Override
  //初始化的时候传入图当中节点的个数
  protected void setup(Context context)
      throws IOException, InterruptedException {
    numberOfNodesInGraph = context.getConfiguration().getInt(
        CONF_NUM_NODES_GRAPH, 0);
  }

  private Text outValue = new Text();
//reduce方法处理第一是map一开始的输出，二是临节点的输出
//对于b节点举例子  一开始B对自己输出，还有A对B的输出，所以当key值为b的时候有2条
  public void reduce(Text key, Iterable<Text> values,
                     Context context)
      throws IOException, InterruptedException {

    System.out.println("input -> K[" + key + "]");

    double summedPageRanks = 0;
    //定义当前的节点
    Node originalNode = new Node();

    for (Text textValue : values) {
      System.out.println("  input -> V[" + textValue + "]");

      Node node = Node.fromMR(textValue.toString());
//      	 通过有没有临界点来判断是否其他节点的打分还是自己的输出
      if (node.containAjacentNodes()) {
        // the original node
        //
        originalNode = node;
      } else {
    	  //是别人的打分就做累加
        summedPageRanks += node.getPageRank();
      }
    }
 //公式前半部分
    double dampingFactor =
        ((1.0 - DAMPING_FACTOR) / (double) numberOfNodesInGraph);
//得到新的pr值
    double newPageRank =
        dampingFactor + (DAMPING_FACTOR * summedPageRanks);
//变化值 老的pr-新的pr
    double delta = originalNode.getPageRank() - newPageRank;

    originalNode.setPageRank(newPageRank);

    outValue.set(originalNode.toString());

    System.out.println(
        "  output -> K[" + key + "],V[" + outValue + "]");
    context.write(key, outValue);

//因为mapreduce提供的一个counter只能是一个整数，得到的差值delta 很可能是一个小数，所以去*1000
    int scaledDelta =
        Math.abs((int) (delta * CONVERGENCE_SCALING_FACTOR));

    System.out.println("Delta = " + scaledDelta);
//一个mapreduce运行完之后是，这个counter放的是所有节点pr值变化的总和 
    context.getCounter(Counter.CONV_DELTAS).increment(scaledDelta);
  }
}
