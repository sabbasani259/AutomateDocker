package remote.wise.businessentity;

public class LanguageTrackingEntity {

	private int trace_id;
	private ContactEntity LoginId;
	private String old_lang;
	private String new_lang;
	private String lang_set_date;
	
	public int getTrace_id() {
		return trace_id;
	}
	public void setTrace_id(int trace_id) {
		this.trace_id = trace_id;
	}
	public ContactEntity getLoginId() {
		return LoginId;
	}
	public void setLoginId(ContactEntity loginId) {
		LoginId = loginId;
	}
	public String getOld_lang() {
		return old_lang;
	}
	public void setOld_lang(String old_lang) {
		this.old_lang = old_lang;
	}
	public String getNew_lang() {
		return new_lang;
	}
	public void setNew_lang(String new_lang) {
		this.new_lang = new_lang;
	}
	public String getLang_set_date() {
		return lang_set_date;
	}
	public void setLang_set_date(String lang_set_date) {
		this.lang_set_date = lang_set_date;
	}
	
	
}
