package cn.jhc.sqq.myblog.lucene;


import cn.jhc.sqq.myblog.entity.Blog;
import cn.jhc.sqq.myblog.utils.DateUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author sqq
 * @ 2019/9/7 13:29
 */
public class BlogIndex {
    private Directory dir = null;
    private String lucenePath = "D:\\sources\\index";

    /*获取对lucene的写入*/
    private IndexWriter getWriter() throws IOException {
        dir = FSDirectory.open(Paths.get(lucenePath, new String[0]));
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        IndexWriter indexWriter = new IndexWriter(dir, config);
        return indexWriter;
    }

    /*增加索引*/
    public void addIndex(Blog blog) throws IOException {
        System.out.println(blog);
        IndexWriter indexWriter = getWriter();
//        无id
        Document doc = new Document();
        doc.add(new TextField("id", String.valueOf(blog.getId()), Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES));
        doc.add(new StringField("releaseDate", DateUtil.formateDate(new Date(), "yyyy-MM-dd"), Field.Store.YES));
        doc.add(new TextField("content", blog.getContentNoTag(), Field.Store.YES));
        doc.add(new TextField("keyWord", blog.getKeyWord(), Field.Store.YES));
        indexWriter.addDocument(doc);
        indexWriter.close();
    }

    /*跟新索引*/
    public void updateIndex(Blog blog) throws IOException {
        IndexWriter indexWriter = getWriter();

        Document doc = new Document();
        doc.add(new TextField("id", String.valueOf(blog.getId()), Field.Store.YES));
        doc.add(new TextField("title", blog.getTitle(), Field.Store.YES));
        doc.add(new StringField("releaseDate", DateUtil.formateDate(new Date(), "yyyy-MM-dd"), Field.Store.YES));
        doc.add(new TextField("content", blog.getContentNoTag(), Field.Store.YES));
        doc.add(new TextField("keyWord", blog.getKeyWord(), Field.Store.YES));

        indexWriter.updateDocument(new Term("id", String.valueOf(blog.getId())), doc);
        indexWriter.close();
    }

    /*删除索引*/
    public void deleteIndex(String blogId) throws IOException {
        IndexWriter indexWriter = getWriter();
        indexWriter.deleteDocuments(new Term[]{new Term("id", blogId)});
        indexWriter.forceMergeDeletes();
        indexWriter.commit();
        indexWriter.close();
    }

    /*搜索索引*/
    public List<Blog> searchBlog(String key ) throws IOException, ParseException, InvalidTokenOffsetsException {
        List<Blog> blogList = new LinkedList<>();
        /*获取reader*/
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(lucenePath)));
        /*获取流*/
        IndexSearcher searcher = new IndexSearcher(reader);

        BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
        SmartChineseAnalyzer analyzer = new SmartChineseAnalyzer();

        QueryParser parser1 = new QueryParser("title", analyzer);
        Query query1 = parser1.parse(key);

        QueryParser parser2 = new QueryParser("content", analyzer);
        Query query2 = parser2.parse(key);

        QueryParser parser3 = new QueryParser("keyWord", analyzer);
        Query query3 = parser3.parse(key);

        /*添加booleanquery的查询条件*/
        booleanQuery.add(query1, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query2, BooleanClause.Occur.SHOULD);
        booleanQuery.add(query3, BooleanClause.Occur.SHOULD);

        /*返回100条数据*/
        TopDocs topDocs = searcher.search(booleanQuery.build(), 100);

        /*高亮显示*/
        QueryScorer scorer = new QueryScorer(query1);
        Fragmenter fragmenter = new SimpleFragmenter(1500);
        SimpleHTMLFormatter htmlFormatter = new SimpleHTMLFormatter("<b><font color='red'>", "</font></b>");
        Highlighter highlighter = new Highlighter(htmlFormatter, scorer);
        highlighter.setTextFragmenter(fragmenter);

        /*遍历查询结果放入blogList*/
        for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
            Document doc = searcher.doc(scoreDoc.doc);
            Blog blog = new Blog();
            blog.setId(Integer.parseInt(doc.get("id")));
            blog.setReleaseDateStr(doc.get("releaseDate"));
            String title = doc.get("title");
            String content = StringEscapeUtils.escapeHtml(doc.get("content"));
            String keyWord = doc.get("keyWord");

            if (title != null) {
                TokenStream tokenStream = analyzer.tokenStream("title", new StringReader(title));
                String hTitle = highlighter.getBestFragment(tokenStream, title);
                if (hTitle == null || hTitle == "") {
                    blog.setTitle(title);
                } else {
                    blog.setTitle(hTitle);
                }
            }

            if (content != null) {
                TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(content));
                String hContent = highlighter.getBestFragment(tokenStream, content);
                System.out.println(hContent);
                if (hContent == null || hContent == "") {
//                    if (content.length() <= 200) {
//                        blog.setContent(content);
//                    } else {
//                        blog.setContent(content.substring(0, 200));
//                    }
                    blog.setContent(content);
                } else {
                    blog.setContent(hContent);
                }
            }

            if (keyWord != null) {
                TokenStream tokenStream = analyzer.tokenStream("keyWord", new StringReader(keyWord));
                String hKeyWord = highlighter.getBestFragment(tokenStream, keyWord);
                if (hKeyWord == null || hKeyWord == "") {
                    blog.setKeyWord(keyWord);
                } else {
                    blog.setKeyWord(hKeyWord);
                }
            }

            blogList.add(blog);
        }
        return blogList;
    }
}
