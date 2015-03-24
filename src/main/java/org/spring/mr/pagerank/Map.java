package org.spring.mr.pagerank;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class Map
    extends Mapper<Text, Text, Text, Text> {

	//定义2个text对象，用来做map的输出
  private Text outKey = new Text();
  private Text outValue = new Text();

  @Override
  protected void map(Text key, Text value, Context context)
      throws IOException, InterruptedException {

	  //map任务一进来就将当前任务做输出
    context.write(key, value);

    Node node = Node.fromMR(value.toString());
    //判断有没有临节点
    if(node.getAdjacentNodeNames() != null &&
        node.getAdjacentNodeNames().length > 0) {
    	//当前的pr值除以临界点的个数
      double outboundPageRank = node.getPageRank() /
          (double)node.getAdjacentNodeNames().length;
      //遍历所有临节点，输出所有临节点，并为他们打分
      for (int i = 0; i < node.getAdjacentNodeNames().length; i++) {

        String neighbor = node.getAdjacentNodeNames()[i];

        outKey.set(neighbor);

        Node adjacentNode = new Node()
            .setPageRank(outboundPageRank);
        //输出没有设置临界点的值，以便和一开始的输出做区分
        outValue.set(adjacentNode.toString());
        System.out.println(
            "  output -> K[" + outKey + "],V[" + outValue + "]");
        context.write(outKey, outValue);
      }
    }
  }
}
