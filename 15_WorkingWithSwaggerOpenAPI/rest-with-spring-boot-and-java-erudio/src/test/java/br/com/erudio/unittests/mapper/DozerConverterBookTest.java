package br.com.erudio.unittests.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.erudio.data.vo.v1.BookVO;
import br.com.erudio.mapper.DozerMapper;
import br.com.erudio.model.Book;
import br.com.erudio.unittests.mapper.mocks.MockBook;

public class DozerConverterBookTest {

	MockBook inputObject;

    @BeforeEach
    public void setUp() {
        inputObject = new MockBook();
    }

    @Test
    public void parseEntityToVOTest() {
        BookVO output = DozerMapper.parseObject(inputObject.mockEntity(), BookVO.class);
        assertEquals(Long.valueOf(0L), output.getKey());
        assertEquals("Title Test0", output.getTitle());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(new Date(output.getKey()), output.getLaunchDate());
        assertEquals(0.0, output.getPrice());
    }

    @Test
    public void parseEntityListToVOListTest() {
        List<BookVO> outputList = DozerMapper.parseListObjects(inputObject.mockEntityList(), BookVO.class);
        
        BookVO outputZero = outputList.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getKey());
        assertEquals("Title Test0", outputZero.getTitle());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(new Date(outputZero.getKey()), outputZero.getLaunchDate());
        assertEquals(0.0, outputZero.getPrice());
        
        BookVO outputSeven = outputList.get(7);
        assertEquals(Long.valueOf(7L), outputSeven.getKey());
        assertEquals("Title Test7", outputSeven.getTitle());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(new Date(outputSeven.getKey()), outputSeven.getLaunchDate());
        assertEquals(70.0, outputSeven.getPrice());
        
        BookVO outputTwelve = outputList.get(12);
        assertEquals(Long.valueOf(12L), outputTwelve.getKey());
        assertEquals("Title Test12", outputTwelve.getTitle());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(new Date(outputTwelve.getKey()), outputTwelve.getLaunchDate());
        assertEquals(120.0, outputTwelve.getPrice());
    }
    
    @Test
    public void parseVOToEntityTest() {
        Book output = DozerMapper.parseObject(inputObject.mockVO(), Book.class);
        assertEquals(Long.valueOf(0L), output.getId());
        assertEquals("Title Test0", output.getTitle());
        assertEquals("Author Test0", output.getAuthor());
        assertEquals(new Date(output.getId()), output.getLaunchDate());
        assertEquals(0.0, output.getPrice());
    }

    @Test
    public void parserVOListToEntityListTest() {
        List<Book> outputList = DozerMapper.parseListObjects(inputObject.mockVOList(), Book.class);
        
        Book outputZero = outputList.get(0);
        assertEquals(Long.valueOf(0L), outputZero.getId());
        assertEquals("Title Test0", outputZero.getTitle());
        assertEquals("Author Test0", outputZero.getAuthor());
        assertEquals(new Date(outputZero.getId()), outputZero.getLaunchDate());
        assertEquals(0.0, outputZero.getPrice());
        
        Book outputSeven = outputList.get(7);
        assertEquals(Long.valueOf(7L), outputSeven.getId());
        assertEquals("Title Test7", outputSeven.getTitle());
        assertEquals("Author Test7", outputSeven.getAuthor());
        assertEquals(new Date(outputSeven.getId()), outputSeven.getLaunchDate());
        assertEquals(70.0, outputSeven.getPrice());
        
        Book outputTwelve = outputList.get(12);
        assertEquals(Long.valueOf(12L), outputTwelve.getId());
        assertEquals("Title Test12", outputTwelve.getTitle());
        assertEquals("Author Test12", outputTwelve.getAuthor());
        assertEquals(new Date(outputTwelve.getId()), outputTwelve.getLaunchDate());
        assertEquals(120.0, outputTwelve.getPrice());
    }
}
