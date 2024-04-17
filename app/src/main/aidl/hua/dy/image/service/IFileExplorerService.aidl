// IFileExplorerService.aidl
package hua.dy.image.service;

import hua.dy.image.bean.FileBean;

// Declare any non-default types here with import statements

interface IFileExplorerService {

    List<FileBean> listFiles(String path);

    FileBean getFileBean(String path);

}