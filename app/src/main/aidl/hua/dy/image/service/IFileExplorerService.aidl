// IFileExplorerService.aidl
package hua.dy.image.service;

import hua.dy.image.bean.FileBean;
import hua.dy.image.bean.ImageBean;
import hua.dy.image.app.AppBean;

// Declare any non-default types here with import statements

interface IFileExplorerService {

    List<FileBean> listFiles(String path);

    FileBean getFileBean(String path);

    ImageBean copyToMyFile(in FileBean bean,long fileSize,int cacheIndex,String providerSecond, String saveImagePath, in List<String> cachePath);

}