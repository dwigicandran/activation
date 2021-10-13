package com.bsms.service.authentication;

import com.amanah.ActivationCode;
import com.bsms.cons.MbApiConstant;
import com.bsms.domain.*;
import com.bsms.repository.*;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.restobjclient.authentication.*;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.*;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Cipher;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Service("obtainActCode")
@Transactional
public class MbObtainActCodeServiceImpl extends MbBaseServiceImpl implements MbService {

    @Autowired
    private MbTxLogRepository txLogRepository;

    @Autowired
    private MbAppContentRepository mbAppContentRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CardmappingRepository cardMappingRepository;

    @Autowired
    private MbAcRequestRepository mbAcRequestRepository;

    @Autowired
    private SettingRepository settingRepository;

    @Autowired
    private SecurityRepository securityRepository;
    @Autowired
    private ErrormsgRepository errormsgRepository;

    @Value("${key_acr}")
    private String key_acr;

    @Value("${pinkeyretrieval.url}")
    private String url;

    @Value("${verify.url}")
    private String url1;

    private Long customerId;
    private String msisdn ;
    private String host_hsm="10.2.129.85";
    private int port_hsm= 9191;
    private String pinOffset;


    @Override
    public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
            throws Exception {

        MbApiTxLog txLog = new MbApiTxLog();
        txLogRepository.save(txLog);

        String responseDesc = null;
        String responseCode = null;
        String language = request.getLanguage();
        String req_data = request.getRequest_data(); // TODO: check req data dari depan
        MbApiResp response= null ;
        String remark = null;


        try {
                String[] msg = req_data.split("_");
                String[] elmt = LibCNCrypt.decrypt(key_acr, msg[1]).split("#");

                String msisdn1, msisdn2;
                msisdn = msg[0];

                if ("0".equals(msg[0].substring(0, 1))) {
                    msisdn1 = msg[0];
                    msisdn2 = "62" + msg[0].substring(1);
                }
                else {
                    msisdn1 = "0" + msg[0].substring(2);
                    msisdn2 = msg[0];
                }

                List<Customer> cust = customerRepository.getByMsisdn(msisdn1, msisdn2);
                if (!cust.isEmpty()) {

                    String createOtpDate = null;
                    String actCode = null;

                    for (Customer getCust : cust) {
                        customerId = getCust.getId();
                        createOtpDate = getCust.getCreateotpdate();
                        actCode = getCust.getActivationcode();
                    }
                    System.out.println(customerId + " ::: CustomerId");
                    System.out.println(actCode + " ::: ActivationCode");
                    System.out.println(createOtpDate + " ::: CreateOTPDate");


                    pinOffset = cardMappingRepository.getPinoffsetByID(Long.toString(customerId));
                    if (pinOffset == null) {
                        if ("id".equalsIgnoreCase(elmt[1]))
                            responseDesc = "Permintaan Anda tidak bisa diproses. Silahkan datang ke Cabang.";
                        else
                            responseDesc = "Your request can not be processed. Please visit our branch.";
                        responseCode = "05";
                        response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
                        remark = "The PIN Offset is blank";
                    }
                    else {
                        String pin = elmt[0];
                        //verify pin
                        MbApiResp resp = verifyPIN(request,msisdn,pin);
                        String rc = resp.getResponseCode();

                        System.out.println(rc + " ::: RC VerifyPin");
                        if ("00".equals(rc)) {
                            if (createOtpDate != null) {
                                org.json.me.JSONObject jsAC = new ActivationCode(host_hsm, port_hsm).generate();
                                actCode = jsAC.getString("mac");
                                customerRepository.updateCustByMsisdn(jsAC.getString("tak"),jsAC.getString("machex"),msisdn1,msisdn2);
                            }
                            if ("00".equals(rc)){
                                if ("id".equalsIgnoreCase(elmt[1]))
                                    responseDesc = "Kode Aktivasi Anda: " + actCode + ", untuk mengaktifkan mobile banking. RAHASIAKAN kode aktivasi anda, termasuk pihak a.n. Bank Syariah Indonesia. Mohon disimpan untuk aktivasi ulang.";
                                else
                                    responseDesc = "Your Activation Code: " + actCode + ", to activate mobile banking. This code is CONFIDENTIAL, including those on behalf of Bank Syariah Indonesia. Please save for re-activation.";
                                responseCode = "00";
                                response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
                                remark = "Successful";
                            }
                        }
                        else
                        if ("03".equals(rc)) {
                            if ("id".equalsIgnoreCase(elmt[1]))
                                responseDesc = "Permintaan Anda tidak bisa diproses. Silahkan datang ke Cabang.";
                            else
                                responseDesc = "Your request can not be processed. Please visit our branch.";
                            responseCode = "03";
                            response = MbJsonUtil.createResponseDesc(request,responseCode,responseDesc);
                            remark = "The PIN has been blocked. No more attempts";
                        }
                        else
                        if ("04".equals(rc)) {
                            if ("id".equalsIgnoreCase(elmt[1]))
                                responseDesc = "PIN yang anda masukkan salah. 3 kali salah input PIN, aplikasi akan diblokir, Silahkan hubungi BSI Cabang terdekat";
                            else
                                responseDesc = "The PIN you entered is wrong. Input the wrong PIN 3 times, the application will be blocked, please contact BSI nearest";
                            responseCode = "04";
                            response = MbJsonUtil.createResponseDesc(request,responseCode,responseDesc);
                            remark = "PIN invalid: 01";
                        }
                        else {
                            responseCode = "05";
                            ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage(responseCode, language);
                            response = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
                        }
                    }
                }
                else {
                    if ("id".equalsIgnoreCase(language))
                        responseDesc = "Nomor Handphone Anda tidak terdaftar sebagai pengguna mobile banking, silahkan hubungi Bank Syariah Indonesia Call 14040";
                    else
                        responseDesc = "Your mobile number is not registered as mobile banking user, please contact Bank Syariah Indonesia Call 14040";
                    responseCode = "01";
                    response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
                    remark = "The msisdn can not be found";
                }


                MbAcRequest mbAcRequestSave = new MbAcRequest();
                mbAcRequestSave.setMsisdn(msisdn);
                mbAcRequestSave.setMessage(String.valueOf(elmt[0].hashCode()));
                mbAcRequestSave.setStatus(responseCode);
                mbAcRequestSave.setRemark(remark);
                mbAcRequestRepository.saveMbAc(msisdn,String.valueOf(elmt[0].hashCode()),responseCode,remark);

            }
            catch (Exception e) {
                MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("600002", language);
                responseDesc = mbAppContent.getDescription();
                responseCode = MbApiConstant.ERR_CODE;
                response = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
            }
            txLog.setResponse(response);
            txLogRepository.save(txLog);

            return response;
        }

        private MbApiResp verifyPIN(MbApiReq request,String msisdn,String pin) throws Exception{
            MbApiResp mbApiResp;
            String responseCode = "";
            String responseDesc = "";
            String language = MbApiConstant.DEFAULT_LANG;
            // request ke hsm pinkeyRetrieval
            PINKeyResp pinKeyResp;
            PINKeyReq pinKeyReq = new PINKeyReq();
            pinKeyReq.setCustomerId(Long.toString(customerId));

            System.out.println(new Gson().toJson(pinKeyReq));
            try {

                HttpEntity<?> req = new HttpEntity(pinKeyReq, RestUtil.getHeaders());
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<PINKeyResp> responseEntity = restTemplate.exchange(url, HttpMethod.POST, req,
                        PINKeyResp.class);
                pinKeyResp = responseEntity.getBody();
                System.out.println(new Gson().toJson(responseEntity));

                String clearZPK = pinKeyResp.getClearZPK();
                String zpkLmk = pinKeyResp.getZpkLmk();

                securityRepository.deleteByCustId(customerId);

                Timestamp ts = new Timestamp(System.currentTimeMillis());
                Date changeTime = ts;
                String sessionId = TrxIdUtil.getTransactionID(6);

                MbLibRSA lib_rsa = new MbLibRSA("RSA/None/PKCS1Padding");
                lib_rsa.GenerateKeypair();
                String public_key = lib_rsa.GetPublicKeyPem();
                String private_key = lib_rsa.GetPrivateKeyPem();

                Security securitySave = new Security();
                securitySave.setCustomerId(customerId);
                securitySave.setZpkLmk(zpkLmk);
                securitySave.setStatus("1");
                securitySave.setChangeTime(changeTime);
                securitySave.setMbDevice(request.getDevice());
                securitySave.setMbDeviceType(request.getDevice_type());
                securitySave.setMbIpAddress(request.getIp_address());
                securitySave.setMbImei(request.getImei());
                securitySave.setMbIccid(request.getIccid());
                securitySave.setMbSessionId(sessionId);
                securitySave.setPrivateKey(private_key);
                securitySave.setMb_PublicKey(public_key);
                securityRepository.save(securitySave);

                System.out.println("Security Saved");

                //verifyPIN
                Integer failedPINCount;
                Long customerId;
                String msisdns = null;

                VerifyPinReq verifyPinReq = new VerifyPinReq();

                Security security = securityRepository.findByMbSessionId(sessionId);
                customerId = security.getCustomerId();
                String ZPK_lmk = security.getZpkLmk();
                String  pin1=getPINBlock(clearZPK,pin);

                String cardNumber = cardMappingRepository.getCardnumberByCustomerId(String.valueOf(customerId));

                String pinOffset1 = String.format("%-12s", LibCNCrypt.decrypt1(pinOffset)).replace(" ","F");

                verifyPinReq.setDevice(request.getDevice());
                verifyPinReq.setDeviceType(request.getDevice_type());
                verifyPinReq.setImei(request.getImei());
                verifyPinReq.setIpAddress(request.getIp_address());
                verifyPinReq.setOsType(request.getOsType());
                verifyPinReq.setOsVersion(request.getOsVersion());
                verifyPinReq.setPin(pin1);
                verifyPinReq.setRequestType(request.getRequest_type());
                verifyPinReq.setVersionName(request.getVersion_name());
                verifyPinReq.setVersionValue(request.getVersion_value());
                verifyPinReq.setCard_number(cardNumber);
                verifyPinReq.setPin_offset(pinOffset1);
                verifyPinReq.setZpk(ZPK_lmk);
                verifyPinReq.setSessionId(sessionId);
                verifyPinReq.setModulId(request.getModul_id());
                verifyPinReq.setSrcAcc(request.getSourceAccountNumber());

                System.out.println(new Gson().toJson(verifyPinReq));

                try {

                    HttpEntity<?> req1 = new HttpEntity(verifyPinReq, RestUtil.getHeaders());
                    RestTemplate restTemps = new RestTemplate();
                    ResponseEntity<VerifyPinResp> response = restTemps.exchange(url1, HttpMethod.POST, req1, VerifyPinResp.class);
                    VerifyPinResp verifyPinResp = response.getBody();

                    failedPINCount = customerRepository.getFailedPINCountById(customerId);
                    if (failedPINCount == null) {
                        failedPINCount = 0;
                    }

                    System.out.println(failedPINCount + " ::: FAILEDPINCOUNT");
                    System.out.println(verifyPinResp.getResponseCode() + " ::: RESPONSE HSM");

                    if ("00".equals(verifyPinResp.getResponseCode())) {

                        if (failedPINCount < 3) {
                            failedPINCount = 0;

                            long msisdnDb = customerRepository.getMsisdnByID(customerId);
                            msisdn = String.valueOf(msisdnDb);

                            StringBuilder sb = new StringBuilder();
                            sb.append("0");
                            sb.append(msisdn);
                            msisdns = sb.toString();

                            // update via jpa
                            Customer custUpd = customerRepository.findTopByMsisdn(msisdns);
                            custUpd.setFailedpincount(String.valueOf(failedPINCount));
                            customerRepository.save(custUpd);

                            VerifyPinRespDisp display = new VerifyPinRespDisp();
                            display.setTransactionId(TrxIdUtil.getTransactionID(6));
                            mbApiResp = MbJsonUtil.createResponse(request, display, verifyPinResp.getResponseCode(), "Verify PIN succesfull");

                        } else {
                            responseCode = "03";
                            responseDesc = "The PIN has been blocked. No more attempts";
                            mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
                        }
                    }
                    else {
                        if ("01".equals(verifyPinResp.getResponseCode())) {

                            long msisdnDb = customerRepository.getMsisdnByID(customerId);
                            msisdn = String.valueOf(msisdnDb);

                            StringBuilder sb = new StringBuilder();
                            sb.append("0");
                            sb.append(msisdn);
                            msisdns = sb.toString();

                            failedPINCount = customerRepository.getFailedPINCountById(customerId);
                            ++failedPINCount;

                            // update via jpa
                            Customer custUpd = customerRepository.findTopByMsisdn(msisdns);
                            custUpd.setFailedpincount(String.valueOf(failedPINCount));
                            customerRepository.save(custUpd);

                            if (failedPINCount < 3) {
                                responseCode = "04";
                                responseDesc = "Wrong PIN";

                            } else {
                                responseCode = "03";
                                responseDesc = "The PIN has been blocked. No more attempts";
                            }

                            mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);

                        } else {
                            responseCode = verifyPinResp.getResponseCode();
                            ErrorMessage errMsg = errormsgRepository.findByCodeAndLanguage("05", language);
                            mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, errMsg.getDescription());
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();

                    MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", language);
                    responseDesc = mbAppContent.getDescription();
                    responseCode = MbApiConstant.ERR_CODE;
                    mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
                }
            }
            catch (Exception e) {

                MbAppContent mbAppContent = mbAppContentRepository.findByLangIdAndLanguage("60002", language);
                responseDesc = mbAppContent.getDescription();
                responseCode = MbApiConstant.ERR_CODE;
                mbApiResp = MbJsonUtil.createResponseDesc(request, responseCode, responseDesc);
            }
            return mbApiResp;
        }

    public static String getPINBlock(String key, String PIN) {
        if (key == null || key.isEmpty()) {
            return "";
        }

        //PIN = padRight(PIN, 16, 'F');
        PIN = String.format("%-16s", PIN).replace(" ", "F");
        //Log.i(TAG, "setPINBlock: PIN " + PIN);
        String pinBlock = "";//null;

        try {


            DESKeySpec key_spec = new DESKeySpec(toByte(key));
            SecretKeySpec DESKey = new SecretKeySpec(key_spec.getKey(), "DES");
            Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, DESKey);
            byte[] encrypted = cipher.doFinal(toByte(PIN));
            pinBlock = toHex(encrypted).toUpperCase();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return pinBlock;
    }
    public static byte[] toByte(String hex) {
        if (hex == null || hex.length() == 0) {
            return null;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return ba;
    }

    // byte[] to hex
    public static String toHex(byte[] ba) {
        if (ba == null || ba.length == 0) {
            return null;
        }

        StringBuffer sb = new StringBuffer(ba.length * 2);
        String hexNumber;
        for (int x = 0; x < ba.length; x++) {
            hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

            sb.append(hexNumber.substring(hexNumber.length() - 2));
        }
        return sb.toString();
    }


    }
