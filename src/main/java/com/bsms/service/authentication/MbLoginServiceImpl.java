package com.bsms.service.authentication;

import com.bsms.cons.MbApiConstant;
import com.bsms.domain.MbApiTxLog;
import com.bsms.domain.MbApiUser;
import com.bsms.filter.MbSecurityContext;
import com.bsms.repository.MbTxLogRepository;
import com.bsms.repository.MbUserRepository;
import com.bsms.restobj.MbApiReq;
import com.bsms.restobj.MbApiResp;
import com.bsms.service.base.MbBaseServiceImpl;
import com.bsms.service.base.MbService;
import com.bsms.util.MbJWTUtil;
import com.bsms.util.MbJsonUtil;
import com.bsms.util.MbPasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.HttpHeaders;
import java.util.Random;

@Service("login")
public class MbLoginServiceImpl extends MbBaseServiceImpl implements MbService {

	@Autowired
	private MbTxLogRepository txLogRepository;

	@Autowired
	private MbUserRepository userRepository;

	@Override
	public MbApiResp process(HttpHeaders header, ContainerRequestContext requestContext, MbApiReq request)
			throws Exception {

		MbApiTxLog txLog = new MbApiTxLog();
		txLog.setRequest(request);
		txLogRepository.save(txLog);
		
		MbApiUser user = userRepository.findOneByUsername(request.getUsername());

		if (user == null)
			throw createSlServiceException("username.not.found", "Username is not found", txLog, txLogRepository);

		if (!MbPasswordUtil.isValid(request.getPassword(), user.getPassword())) {
			throw createSlServiceException("username.password.unmatch", "Username and password do not match", txLog,
					txLogRepository);
		}

		MbApiUser userJwt = new MbApiUser();
		userJwt.setId(user.getId());
		userJwt.setUsername(user.getUsername());
		userJwt.setBatchToken(user.getBatchToken());

		try {
			MbSecurityContext securityContext = (MbSecurityContext) requestContext.getSecurityContext();
			securityContext.setUserPrinsipal(userJwt);
			securityContext.setAccessToken(
					MbJWTUtil.generateJWT(userJwt, MbApiConstant.JWT_EXPIRATION_TIME, createRandomCharacter(20)));
		} catch (Exception e) {
			e.printStackTrace();
		}

		MbApiResp response = MbJsonUtil.createResponse(request,
				MbJWTUtil.generateJWT(userJwt, MbApiConstant.JWT_EXPIRATION_TIME, createRandomCharacter(20)));

		txLog.setResponse(response);
		txLogRepository.save(txLog);

		return response;
	}

	public String createRandomCharacter(int length) {

		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			char c = MbApiConstant.CHAR_FOR_RANDOMIZE[random.nextInt(MbApiConstant.CHAR_FOR_RANDOMIZE.length)];
			sb.append(c);
		}
		return sb.toString();
	}

}
