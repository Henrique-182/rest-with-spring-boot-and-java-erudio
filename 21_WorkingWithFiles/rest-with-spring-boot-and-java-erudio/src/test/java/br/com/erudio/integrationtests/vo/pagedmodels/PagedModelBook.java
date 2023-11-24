package br.com.erudio.integrationtests.vo.pagedmodels;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import br.com.erudio.integrationtests.vo.BookVO;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PagedModelBook implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "content")
	private List<BookVO> content;

	public PagedModelBook() {}

	public List<BookVO> getContent() {
		return content;
	}

	public void setContent(List<BookVO> content) {
		this.content = content;
	}

	@Override
	public int hashCode() {
		return Objects.hash(content);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PagedModelBook other = (PagedModelBook) obj;
		return Objects.equals(content, other.content);
	}
	
}
