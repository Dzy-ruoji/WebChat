package com.waxsb.util.test;


import java.io.*;
import java.util.Scanner;

public class UploadFile {
    public static void main(String[] args) throws IOException {
        //需求：模拟用户上传头像的功能，假设所有的用户头像都应该上传到：项目下的lib文件中
        //1.定义一个方法，用来获取要上传的用户头像路径 getPath()
        File path = getPath();
        System.out.println(path);
        //2.定义一个方法，用来判断要上传的用户头像已经存在，上传失败
        boolean flag = isExists(path.getName());
        //上传头像
        uploadFile(path);



    }

    //获取上传的用户头像路径
    public static File getPath(){
        //1.提示用户录入要上传的用户头像路径，并接收
          Scanner sc=new Scanner(System.in);
          while(true){
              System.out.println("请录入您要上传的用户头像路径");
              String path = sc.nextLine();
              //2.判断该路径的后缀名是否是：.jpg .png .bmp
              //3.如果不是，提示您录入的不是图片
              if(!path.endsWith(".jpg")&&!path.endsWith(".png")&&!path.endsWith("bmp")){
                  System.out.println("您录入的不是图片，请重新录入");
                  continue;
              }
              //4.如果是，判断路径是否存在，并且是否为文件
              File file = new File(path);
              if(file.exists()&&file.isFile()){
                  //6.如果是，就是我们想要的数据
                  return file;
              }else{
                  //.如果不是文件，提示您录入的路径不合法，请重新录入
                  System.out.println("您录入的路径不合法，请重新录入");
              }

          }
    }

    //判断要上传的用户头像，在lib文件中是否存在
    public static boolean isExists(String path){
        //1.将lib文件夹封装成File对象
        File file = new File("lib");
        //2.获取lib文件夹中所有的文件的名称数据
        String[] names = file.list();

        //3.遍历第二步获取到的数据，用获取到的数据依次和path进行
        for(String name:names){
            String id = name.substring(0, name.lastIndexOf("."));

            if(name.equals(path)){
                //4.如果一致，说明该用户的头像已经存在了，需要覆盖
                return true;
            }
        }
        return false;
        //5.如果不一致，说明该用户头像不存在
    }

    //定义方法，用来上传具体的用户头像
    public static void uploadFile(File path) throws IOException {
        //1.创建字节输入流，关联数据源文件
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        //2.创建字节输出流，关联目的地文件
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("lib/"+path.getName()));
        //3.定义变量，记录读取到的数据
        int len;
        //4.循环读取，只要条件满足就一直读
         while ((len=bis.read())!=-1){
             //5.将读取到的文件写入到目的地文件中
             bos.write(len);
         }

        //6.释放资源
        bis.close();
         bos.close();
         System.out.println("上传成功！");
    }
}
