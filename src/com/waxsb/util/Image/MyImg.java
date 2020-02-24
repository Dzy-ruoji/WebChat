package com.waxsb.util.Image;
import java.io.*;
public  class MyImg {


    //有图片就返回图片名称，无图像就返回默认图片名称
    public static String getImage(String path,String realPath) throws IOException {
        String src;
        boolean flag = isExists(path,realPath);
        //已经上传头像
        if(flag){
            src=path;
        }else{
            //没上传头像，展示默认头像
            src="default.jpg";
        }
        return src;
    }

    //根据绝对路径上传图片,根据id修改图片名称
    public static String uploadImage(String src,String realPath,int id) throws IOException {
        File file = getPath(src);
        //上传时修改名字，返回修改后的字符串
        String image_src = uploadFile(file,realPath,id);

        return image_src;
    }


    //判断绝对路径下的图片是否存在（猜想：可以在前端判断读取的文件）
    public static File getPath(String path){
        //1.提示用户录入要上传的用户头像路径，并接收
        while(true){

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
    public static boolean isExists(String src,String realPath) throws IOException {
        //1.将lib文件夹封装成File对象

        //绝对路径
        //File file = new File("S:\\新建文件夹 (3)\\src\\lib");
       // File file = new File("S:\\新建文件夹 (3)\\src\\com\\waxsb\\util\\Image");
        File file=new File(realPath);

        //2.获取lib文件夹中所有的文件的名称数据
        String[] names = file.list();
            System.out.println(names);

            try {
            //3.遍历第二步获取到的数据，用获取到的数据依次和path进行
            for(String name:names){
                if(name.equals(src)){
                    //4.如果一致，说明该用户的头像已经存在了，需要覆盖/展示
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        //5.如果不一致，说明该用户头像不存在/展示默认头像
    }

    //定义方法，用来上传具体的用户头像
    public static String uploadFile(File path,String realPath,int id) throws IOException {
        //1.创建字节输入流，关联数据源文件
        BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
        //2.创建字节输出流，关联目的地文件

        //修改文件名称
        String src=path.getName();
        //获取后缀名
        src= src.substring(src.lastIndexOf("."));
        //改名为id名.后缀名
        src=id+src;

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(realPath+"\\"+src));
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
        return src;
    }

}
