package com.stock.ctrl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import com.stock.service.StockGetDataService;

@Controller
@RequestMapping("/gatest")
public class GaTestController extends MultiActionController {

	@Autowired(required = false)
	@Qualifier("stockService")
	private StockGetDataService stockService;


	/**
	 * 创建索引测试用
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(params = "method=getPara")
	public ModelAndView fetchData(HttpServletRequest request,
			HttpServletResponse response) {

		Enumeration enu = request.getParameterNames();
		while (enu.hasMoreElements()) {
			String paraName = (String) enu.nextElement();
			System.out
					.println(paraName + ": " + request.getParameter(paraName));
		}
		try {

		} catch (Exception e) {

			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return null;
	}

	public static void formatFlie(String fileName, String context) {
		File f = new File(fileName);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		BufferedWriter output = null;
		try {
			output = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(fileName, true)));
			output.write(context);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// /**
	// *删除索引测试用
	// * @param request
	// * @param response
	// * @return
	// * @throws Exception
	// */
	// @RequestMapping(params = "method=deleteIndex")
	// public ModelAndView deleteIndex(HttpServletRequest request,
	// HttpServletResponse response){
	//
	// String name=CommonTool.obj2String(request.getParameter("name"));
	// String content =CommonTool.obj2String(request.getParameter("content"));
	// String id =CommonTool.obj2String(request.getParameter("indexid"));
	// String catageoryid=
	// CommonTool.obj2String(request.getParameter("catageoryid"));
	// String createby= CommonTool.obj2String(request.getParameter("createby"));
	// String user_Id= CommonTool.obj2String(request.getParameter("user_Id"));
	// String keyword= CommonTool.obj2String(request.getParameter("keyword"));
	// IndexArgument arg= new IndexArgument();
	// arg.setContent(content);
	// arg.setName(name);
	// arg.setId(id);
	// arg.setKeyword(keyword);
	// arg.setCreateby(createby);
	// arg.setCatageoryid(catageoryid);
	// arg.setUser_Id(user_Id);
	// try {
	// indexService.deleteIndex(arg);
	// request.setAttribute("isSuccess", "删除索引成功");
	// request.setAttribute("formcontroller", "1");
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// throw new RuntimeException(e);
	// }
	//
	// return new ModelAndView("page/manageindex");
	// }
	//
	//
	//
	// /**
	// *跳转页面用于查询首页
	// * @param request
	// * @param response
	// * @return
	// * @throws Exception
	// */
	// @RequestMapping(params = "method=queryIndexint")
	// public ModelAndView queryIndexint(HttpServletRequest request,
	// HttpServletResponse response){
	//
	//
	// String classId=CommonTool.obj2String(request.getParameter("classId"));
	// KmClassiFiedDto dtoarg= new KmClassiFiedDto();
	// dtoarg.setClassId(classId);
	// KmTreeDto kmtreenamedto= kmKnowledgeService.queryTreeName(dtoarg);
	// request.setAttribute("kmtreenamedto", kmtreenamedto);
	// request.setAttribute("qcontent", "tag:"+kmtreenamedto.getName());
	// request.setAttribute("kmKnowledgeHotNumber",
	// Constants.getKmKnowledgeHotNumberList());
	// request.setAttribute("classId", classId);
	// return new ModelAndView("page/queryindex");
	// }
	// /**
	// *跳转页面用于查询首页没有类别查询的页面
	// * @param request
	// * @param response
	// * @return
	// * @throws Exception
	// */
	// @RequestMapping(params = "method=queryIndexnotreeint")
	// public ModelAndView queryIndexnotreeint(HttpServletRequest request,
	// HttpServletResponse response){
	//
	//
	// String qcontent=CommonTool.obj2String(request.getParameter("qcontent"));
	// request.setAttribute("qcontent", qcontent);
	// request.setAttribute("kmKnowledgeHotNumber",
	// Constants.getKmKnowledgeHotNumberList());
	//
	// return new ModelAndView("page/queryindexnotree");
	// }
	//
	//
	// @RequestMapping(params = "method=test1")
	// public ModelAndView test1(HttpServletRequest request,
	// HttpServletResponse response){
	//
	//
	//
	//
	//
	// try {
	// String strPdfPath = new String("D://aa.pdf");
	// //判断该路径下的文件是否存在
	// File file = new File(strPdfPath);
	// if (file.exists()) {
	// DataOutputStream temps = new DataOutputStream(response
	// .getOutputStream());
	// DataInputStream in = new DataInputStream(
	// new FileInputStream(strPdfPath));
	//
	// byte[] b = new byte[2048];
	// while ((in.read(b)) != -1) {
	// temps.write(b);
	// temps.flush();
	// }
	//
	// in.close();
	// temps.close();
	// } else {
	//
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// response.setContentType("application/pdf");
	// return new ModelAndView("page/test/manageindex");
	// }
	//
	//
	//
	//
	// /**
	// * 搜索 List 数据
	// * @param request
	// * @param response
	// * @return
	// * @throws Exception
	// */
	// @RequestMapping(value="/*.do",params = "method=searchList")
	// public ModelAndView searchList(HttpServletRequest request,
	// HttpServletResponse response) throws Exception{
	// String q=CommonTool.obj2String(request.getParameter("qcontent"));
	// String
	// catageoryid=CommonTool.obj2String(request.getParameter("catageoryid"));
	//
	// String
	// istagquery=CommonTool.obj2String(request.getParameter("istagquery"));
	// if(q.equals("")){
	// q=Constants.DefaultKeyWord;
	// }
	// int
	// pageIndex=CommonTool.obj2IntegerForPageIndex(request.getParameter("pageIndex"));
	//
	// IndexDtoResult rs=new IndexDtoResult();
	// IndexArgument argument=new IndexArgument();
	// PageUtil.setCriteria(argument, pageIndex);
	// try {
	// if(!q.contains("tag:")){
	// argument.setKeyword(q);//设置关键词查询
	// if(istagquery.equals("1")){
	// //取得大类别id用于在改类别下进行关键词搜索，由于现在只在大类别下进行搜索，区别classId，classId会随着用户的点击类别而进行变化
	//
	// argument.setIstagquery("1");
	// argument.setCatageoryid(catageoryid);
	// }
	// rs.setRecordCount(indexService.queryIndexcount(argument));
	// rs.setIndexDtos(indexService.queryIndex(argument));
	// rs.setQcontent(q);
	// rs.setIscontaintag("0");
	// }
	// else{
	// String classId="";
	// q=q.replaceAll("tag:", "");
	// KmClassiFiedDto dto=new KmClassiFiedDto();
	// if(catageoryid.equals("")){
	// dto.setClassId(null);
	// }
	// else{
	// dto.setClassId(catageoryid);
	// }
	// dto.setClassifiedName(q);
	// //根据名字去找类别id，会有重复名字的类别id，如tag：规则 在首页下，搜索全部的类别的 规则
	// 这类别，在其他大分类下，就搜索这个大分类下的规则
	// List<KmClassiFiedDto>
	// classiFiedDtolist=myWisdomService.queryClassnameByClassId(dto);
	// if(classiFiedDtolist==null|| classiFiedDtolist.size()==0){//如果没有该分类
	// rs.setRecordCount(0);
	// rs.setIndexDtos(Collections.emptyList());
	// rs.setIscontaintag("-1");//-1代表没有该类别
	//
	// }
	// else{
	//
	// for(int i=0;i<classiFiedDtolist.size();i++){
	// classId=classiFiedDtolist.get(i).getClassId()+","+classId;
	// }
	//
	// argument.setKeyword(classId);//设置关键词查询
	// rs.setRecordCount(indexService.queryIndexByTagcount(argument));
	// rs.setIndexDtos(indexService.queryIndexByTag(argument));
	// rs.setIscontaintag("1");
	// }
	// rs.setQcontent(q);
	// }
	// } catch (Exception e) {
	//
	// e.printStackTrace();
	// throw new RuntimeException(e);
	// }
	//
	//
	// PageUtil.setPageInfo(rs,pageIndex);
	// request.setCharacterEncoding("UTF-8");
	// response.setContentType("text/xml;charset=utf-8");
	//
	// JsonUtil.outputJson(rs, response);
	//
	// return null;
	//
	// }
	//
	//
	// /**
	// * 搜索 自动补全list数据
	// * @param request
	// * @param response
	// * @return
	// * @throws Exception
	// */
	// @RequestMapping(value="/*.do",params = "method=searchAutoCompleteList")
	// public ModelAndView searchAutoCompleteList(HttpServletRequest request,
	// HttpServletResponse response) throws Exception{
	// String q=CommonTool.obj2String(request.getParameter("qcontent"));
	//
	// if(q.equals("")){
	// q=Constants.DefaultKeyWord;
	// }
	// int
	// pageIndex=CommonTool.obj2IntegerForPageIndex(request.getParameter("pageIndex"));
	//
	// IndexDtoResult rs=new IndexDtoResult();
	// IndexArgument argument=new IndexArgument();
	// PageUtil.setCriteria(argument, pageIndex);
	// try {
	//
	// argument.setKeyword(q);//设置关键词查询
	//
	// rs.setIndexDtos(indexService.queryIndexAutoComplete(argument));
	// rs.setQcontent(q);
	//
	//
	//
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	//
	// PageUtil.setPageInfo(rs,pageIndex);
	// request.setCharacterEncoding("UTF-8");
	// response.setContentType("text/xml;charset=utf-8");
	//
	// JsonUtil.outputJson(rs, response);
	// //request.setAttribute("searchStr", searchStr);
	// return null;
	//
	// }
	//
	//
	// /**
	// * 搜索树
	// * @param request
	// * @param response
	// * @return
	// * @throws Exception
	// */
	// @RequestMapping(value="/*.do",params = "method=searchTreeList")
	// public ModelAndView searchTreeList(HttpServletRequest request,
	// HttpServletResponse response,ModelMap model) throws Exception{
	// request.setCharacterEncoding("UTF-8");
	// response.setContentType("text/xml;charset=utf-8");
	// String classId=CommonTool.obj2String(request.getParameter("classId"));
	// if(classId.equals("")){
	// classId="004";
	// }
	//
	// try {
	// KmClassiFiedDto dto = new KmClassiFiedDto();
	// dto.setClassId(classId);
	// dto.setLength(classId.length());
	// ClassTreeDtoResult rs = new ClassTreeDtoResult();
	// rs.setList(kmKnowledgeService.queryTreeList(dto));
	//
	//
	// model.put("list",rs);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// throw new RuntimeException(e);
	// }
	//
	// return new ModelAndView("page/leftreeindex");
	//
	// }

}
