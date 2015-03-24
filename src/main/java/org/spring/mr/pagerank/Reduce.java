package org.spring.mr.pagerank;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class Reduce
    extends Reducer<Text, Text, Text, Text> {

  public static final double CONVERGENCE_SCALING_FACTOR = 1000.0;
  //0.85��ϵ��
  public static final double DAMPING_FACTOR = 0.85;
  public static String CONF_NUM_NODES_GRAPH = "pagerank.numnodes";
  private int numberOfNodesInGraph;

  public static enum Counter {
    CONV_DELTAS
  }

  @Override
  //��ʼ����ʱ����ͼ���нڵ�ĸ���
  protected void setup(Context context)
      throws IOException, InterruptedException {
    numberOfNodesInGraph = context.getConfiguration().getInt(
        CONF_NUM_NODES_GRAPH, 0);
  }

  private Text outValue = new Text();
//reduce���������һ��mapһ��ʼ������������ٽڵ�����
//����b�ڵ������  һ��ʼB���Լ����������A��B����������Ե�keyֵΪb��ʱ����2��
  public void reduce(Text key, Iterable<Text> values,
                     Context context)
      throws IOException, InterruptedException {

    System.out.println("input -> K[" + key + "]");

    double summedPageRanks = 0;
    //���嵱ǰ�Ľڵ�
    Node originalNode = new Node();

    for (Text textValue : values) {
      System.out.println("  input -> V[" + textValue + "]");

      Node node = Node.fromMR(textValue.toString());
//      	 ͨ����û���ٽ�����ж��Ƿ������ڵ�Ĵ�ֻ����Լ������
      if (node.containAjacentNodes()) {
        // the original node
        //
        originalNode = node;
      } else {
    	  //�Ǳ��˵Ĵ�־����ۼ�
        summedPageRanks += node.getPageRank();
      }
    }
 //��ʽǰ�벿��
    double dampingFactor =
        ((1.0 - DAMPING_FACTOR) / (double) numberOfNodesInGraph);
//�õ��µ�prֵ
    double newPageRank =
        dampingFactor + (DAMPING_FACTOR * summedPageRanks);
//�仯ֵ �ϵ�pr-�µ�pr
    double delta = originalNode.getPageRank() - newPageRank;

    originalNode.setPageRank(newPageRank);

    outValue.set(originalNode.toString());

    System.out.println(
        "  output -> K[" + key + "],V[" + outValue + "]");
    context.write(key, outValue);

//��Ϊmapreduce�ṩ��һ��counterֻ����һ���������õ��Ĳ�ֵdelta �ܿ�����һ��С��������ȥ*1000
    int scaledDelta =
        Math.abs((int) (delta * CONVERGENCE_SCALING_FACTOR));

    System.out.println("Delta = " + scaledDelta);
//һ��mapreduce������֮���ǣ����counter�ŵ������нڵ�prֵ�仯���ܺ� 
    context.getCounter(Counter.CONV_DELTAS).increment(scaledDelta);
  }
}
