package org.spring.mr.pagerank;


import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class Map
    extends Mapper<Text, Text, Text, Text> {

	//����2��text����������map�����
  private Text outKey = new Text();
  private Text outValue = new Text();

  @Override
  protected void map(Text key, Text value, Context context)
      throws IOException, InterruptedException {

	  //map����һ�����ͽ���ǰ���������
    context.write(key, value);

    Node node = Node.fromMR(value.toString());
    //�ж���û���ٽڵ�
    if(node.getAdjacentNodeNames() != null &&
        node.getAdjacentNodeNames().length > 0) {
    	//��ǰ��prֵ�����ٽ��ĸ���
      double outboundPageRank = node.getPageRank() /
          (double)node.getAdjacentNodeNames().length;
      //���������ٽڵ㣬��������ٽڵ㣬��Ϊ���Ǵ��
      for (int i = 0; i < node.getAdjacentNodeNames().length; i++) {

        String neighbor = node.getAdjacentNodeNames()[i];

        outKey.set(neighbor);

        Node adjacentNode = new Node()
            .setPageRank(outboundPageRank);
        //���û�������ٽ���ֵ���Ա��һ��ʼ�����������
        outValue.set(adjacentNode.toString());
        System.out.println(
            "  output -> K[" + outKey + "],V[" + outValue + "]");
        context.write(outKey, outValue);
      }
    }
  }
}
