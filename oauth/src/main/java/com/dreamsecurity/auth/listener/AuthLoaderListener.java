package com.dreamsecurity.auth.listener;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dreamsecurity.jcaos.Environment;
import com.dreamsecurity.jcaos.jce.provider.JCAOSProvider;
import com.dreamsecurity.jcaos.pkcs.PKCS8;
import com.dreamsecurity.jcaos.util.encoders.Base64;
import com.dreamsecurity.util.CheckUtil;

public class AuthLoaderListener implements ServletContextListener {

	private static final Logger logger = LoggerFactory.getLogger(AuthLoaderListener.class);

	@SuppressWarnings("deprecation")
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		// TODO Auto-generated method stub

		ServletContext context = sce.getServletContext();
		String licensePath = context.getInitParameter("licensePath");

		try {
			logger.info("### JCAOSCrypto Custom licensePath : " + licensePath);
			logger.info("### JCAOSCrypto Initialize ...");
			
			JCAOSProvider.installProvider(false);
			
			if(CheckUtil.isNotEmpty(licensePath)) {
				Environment.setLicensePath(licensePath);
			}

			String strKey = "MIIC7jBIBgkqhkiG9w0BBQ0wOzAbBgkqhkiG9w0BBQwwDgQI+SEqimklhpQCAgQAMBwGCCqDGoaNIQEUBBBtJ3TALh7QftlNQ9yUQebwBIICoLSlpxh3Xda2BZQ2qLoulXKFPZeCf+V6GUhg/kFJZ0EX+IqQq3z6Rgx5nUR7Osz62OMY8zfmKQkXi9UxW2toea+j1+LOADUUqVWrBPDX7tXV4mxg8qLlS9mowY4xwkZU6prmSi5tnhAEq+mzy8SB2lu45jhe4FxxRtyr1RvOLQUyeB2bVNmHpAeOKlqc1UfxSf9HPK0PqKsNiNv2m5tCWMt4aB+HRbFKoErZix2xz3WMGafT+wneMJ7ikG/8Fq5ScSovG0iHlF8mObZVwuWzx5ajonxzywYrpwKnFYdHQ9sGFnYELMMWV4FSjxN5Y0UDHyydK5G1v3Qw0CMI1eABa9S1j3pFcf8nKH7DWbitbcU1YDeMvvl9z6PSCPDfI+nskiZMAIEWvqWa23N/urnyLxWE1xC5/r932CY2MdUKx7FZaJ07NXUQ/6FNB7UBOiwuHPEMgDVCBxJcDi4+Tzo3PF4qAnCubz5CPcLTWlwKVbwwXYPTdZaUhdYOgn81rLyobb1FJ3gLPZN2hDHwzBcBaETY+JEXotm5JWtjDFNMOwPyPVRHecE/uNmte8V43ZZTR65Jcs5GUUpz2ZhuY+BulxzZCJy/RZGDUDDkxHLF19IlRCQSiL8QceDwFO2F9+J0qbmrUdYHjxeEFWRoMMBjNb7ASUz8UhkB5ifSjAxR2foViF/ILsKLSR11glQabij/Tm5/E5b4I2CwIboAMjexb9MrqiIIBVAOOSGD2DdU8RRC4kJ+ix0vg2BGF/wXD2FtKjlFxQUrzTpDxOTWauRch+fAbsmUInA9DXXeTY6wZKV3vwPlSelWY7YjorNRWR/pKO+Dft+6rDo15Fv+W9WwRqRWTivNa3MxkgA/6a3HKPbBE5c4L9LI7ELGxlDYBh61qg==";
			byte[] baKey = Base64.decode(strKey);
			PKCS8 pkcs8 = new PKCS8("test1234");
			pkcs8.decrypt(baKey);
			
			logger.info("### MagicJCrypto 라이센스 검증 완료 ...");

		} catch (Exception e) {

			logger.error("### MagicJCrypto Initialize Failure");
			logger.error("### Check License or java.library.path (LIBPATH | LD_LIBRARY_PATH | SHLIB_PATH)");
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// TODO Auto-generated method stub
		
	}

}
