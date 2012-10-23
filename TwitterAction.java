import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

import twitter4j.Twitter;
import twitter4j.TwitterException;

import common.lib.lang.MessageException;

public class TwitterAction extends MultiActionController {
	/** logger instance */
	private static final Log log = LogFactory.getLog(TwitterAction.class);
	private TwitterAction twitterManager = null;

	public TwitterAction() {

	}

	private MemberManager memberManager = null;
			 
	public void setMemberManager(MemberManager memberManager){
		this.memberManager = memberManager;
	}
	 
	@SuppressWarnings("finally")
	public ModelAndView insertTwitter(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		Map model = new HashMap();
		
		Map reqMap = WebUtil.getRequestMap(request);
		
		String cot_art_id  = StringUtils.defaultString(request.getParameter("cotArtId"));
		String cot_content = StringUtils.defaultString(request.getParameter("cotContent"));
		String callback    = StringUtils.defaultString(request.getParameter("callback"));
		String strLocale   = WebUtil.getLocale(request);
		
		//Twitter URL Connection added
		String strUrl = StringUtils.defaultString(request.getParameter("twitterurl"));
		if(strUrl != null && !"".equals(strUrl)) cot_content += strUrl;
		try {
			String userId  = SessionUtil.getSessionInfo(request, response, "memInfoSeq");
			String twit_id = SessionUtil.getSessionInfo(request, response, "memInfoTwitterID");
			String pass    = "";

			MemberEntity member = new MemberEntity();
			if(twit_id!=null || !"".equals(twit_id)){
				member.setMemInfoSeq(userId);
				member.setMemInfoTwitterID(twit_id);
				pass = this.memberManager.getTwitterPass(member);
				System.out.println("twitter account > userId : "+userId+", "+"twit_id :  "+twit_id+", twit_pw : "+pass);

				if(!"".equals(pass)) {
					
					Twitter twitter = new Twitter(twit_id, pass);

					twitter.updateStatus(cot_content);
					request.setAttribute("result", "S");
					request.setAttribute("message", WebUtil.getMessage(request, "twitter.twitter_"+strLocale, "twitter.success"));
					
					request.setAttribute("callback", callback);
				}
				else  {
					request.setAttribute("message", WebUtil.getMessage(request, "message_"+strLocale, "js.map.enter.twitter_pw"));
				}
			}else{ //twit_id == null
				request.setAttribute("message", WebUtil.getMessage(request, "message_"+strLocale, "js.map.enter.twitter_acnt"));
			}
		} catch (TwitterException e) {
			e.printStackTrace();
			//401 Unauthorized: Authentication credentials were missing or incorrect. 
		}
	}
	
}