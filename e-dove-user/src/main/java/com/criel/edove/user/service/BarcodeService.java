package com.criel.edove.user.service;

import com.criel.edove.user.vo.IdentityBarcodeBase64VO;
import com.criel.edove.user.vo.IdentityBarcodeVO;
import com.criel.edove.user.vo.VerifyBarcodeVO;
import com.google.zxing.WriterException;

import java.io.IOException;

/**
 * 条形码服务
 */
public interface BarcodeService {

    IdentityBarcodeBase64VO generateUserBarcodeBase64() throws IOException, WriterException;

    VerifyBarcodeVO verifyIdentityBarcode(String code);

    IdentityBarcodeVO generateUserBarcode();
}
