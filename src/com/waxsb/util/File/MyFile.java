package com.waxsb.util.File;

import com.waxsb.model.User;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.List;

public class MyFile {
    public String uploadFile(HttpServletRequest req){
        //获取当前项目路径
        User user = (User) req.getSession().getAttribute("user");
        int id = user.getId();
        String img_src=null;
        String realPath=req.getServletContext().getRealPath("/Image");
        int i = realPath.indexOf("out\\artifacts\\__3__war_exploded\\Image");
        realPath = realPath.substring(0, i);
        System.out.println(realPath+"realPath");


        //判断请求是否为multipart请求
        if(! ServletFileUpload.isMultipartContent(req)){
            throw new RuntimeException("当前请求不支持文件上传");
        }
        try {
            //创建一个FileItem工厂
            DiskFileItemFactory factory = new DiskFileItemFactory();

            //设置临时文件的边界值，大于该值上传文件会先保存在临时文件中，否则，上传文件先保存在临时文件中，否则，上传文件将直接写入内存
            factory.setSizeThreshold(1024*1024*10);//1M
            //设置临时文件
            String tempPath = req.getServletContext().getRealPath("/temp");
            File temp = new File(tempPath);
            factory.setRepository(temp);

            //创建文件上传核心组件
            ServletFileUpload upload = new ServletFileUpload(factory);

            //设置每一个item的头部字符编码，其可以防止解决文件名的中文乱码问题
            upload.setHeaderEncoding("UTF-8");

            //设置单个文件上传的最大边界值为2M
            upload.setFileSizeMax(1024*1024*10);

            //解析请求
            List<FileItem> items = upload.parseRequest(req);
            //遍历 items
            for (FileItem item:items){
                if(item.isFormField()){//若item为普通表单项
                    String fieldName = item.getFieldName();//获取表单项名称
                    String fieldValue = item.getString("UTF-8");//获取表单项的值
                    System.out.println(fieldName+"="+fieldValue);
                }else {
                    String fileName = item.getName();//获取上传文件的文件名称
                    int index = fileName.indexOf(".");
                    String formatName = fileName.substring(index);
                    System.out.println(formatName+"formatName");

                    //获取输入流，其中有上传文件的内容
                    InputStream is = item.getInputStream();
                    //获取文件保存在服务器的路径

                    //String path =this.getServletContext().getRealPath("/Image");
                    //创建目标文件，用于保存上传文件
                    File descFile = new File(realPath+"web\\Image",id+formatName);
                    img_src=id+formatName;
                    System.out.println(img_src);
                    //创建文件输出流
                    FileOutputStream os = new FileOutputStream(descFile);
                    //将输入流中的数据写入到输出流中
                    int len=-1;
                    byte [] buf= new byte[1024];
                    while ((len = is.read(buf))!=-1){
                        os.write(buf,0,len);
                    }
                    //关闭流
                    os.close();
                    is.close();
                    //删除临时文件
                    item.delete();
                }

                System.out.println("上传流程结束");
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img_src;
    }
}
