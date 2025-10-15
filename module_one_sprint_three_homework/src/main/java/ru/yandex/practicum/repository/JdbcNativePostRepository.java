package ru.yandex.practicum.repository;


import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.DTO.PostDTO;
import ru.yandex.practicum.mapping.PostRowMapper;
import ru.yandex.practicum.mapping.TagRowMapper;
import ru.yandex.practicum.model.Post;

import java.sql.PreparedStatement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Repository
public class JdbcNativePostRepository implements PostRepository {

    private final JdbcTemplate jdbcTemplate;
    private final PostRowMapper postRowMapper;
    private final TagRowMapper tagRowMapper;
    private static final String SelectSQL = "SELECT id, title, text, image, likesCount, COUNT(*) OVER() AS total_records FROM posts";
    private static final String InsertSQL="insert into posts(title, text) values(?, ?)";
    private static final String UpdateByIdSQL = "update posts set title = ?, text = ?  where id = ?";
    private static final String UpdateImageByIdSQL = "update posts set image = ? where id = ?";
    private static final String SelectByIdSQL = "SELECT *, COUNT(*) OVER() AS total_records FROM posts WHERE id = ?";
    private static final String DeleteByIdSQL = "delete from posts where id = ?";
    private static final String InsertTagSQL="insert into tags(tag) values(?)";
    private static final String InsertPostTagSQL="insert into postsandtags (post,tag) values(?, ?)";
    private static final String DeleteTagsByPostIdSQL = "delete from postsandtags where post = ?";
    private static final String SelectTagsByPostIdSQL = "SELECT tags.tag FROM tags, postsandtags WHERE postsandtags.post = ? and postsandtags.tag = tags.id";
    private static final String SelectTagIdByTagSQL ="SELECT id FROM tags WHERE tag ILIKE ?";
    private static final String IncrementlikesByIdSQL="UPDATE posts SET likesCount = likesCount+1 WHERE id=?";
    private static final String SelectlikesByIdSQL="SELECT likesCount FROM posts WHERE id=?";
    private static final String SelectPostsCommentsCountByIdSQL="SELECT COUNT(*) FROM postsandtags WHERE post=?";
    private static final String SelectFileSuffixFromSeqSQL ="SELECT NEXTVAL('image_sequence')";


    private static final String titleForming = "CASE WHEN LENGTH(posts.title)>128 THEN LEFT(posts.title,128)||'...' ELSE posts.title END AS title";
    private static final String headOfSelectSQL = "Select posts.id, "+titleForming+", posts.text, posts.image, posts.likesCount, COUNT(*) OVER() AS total_records from posts";
    private static final String joinFromPartOfSelectSQL = ",postsandtags,tags";
    private static final String joinWherePartOfSelectSQL ="posts.id = postsandtags.post AND tags.id=postsandtags.tag AND ";
    private static final String initialWherePrefixSQL = " WHERE ";
    private static final String andPrefixSQL = " AND ";
    private static final String tagIlikeSQL = "tags.tag ILIKE ";
    private static final String searchIlikeSQL = "posts.title ILIKE ";
    private static final String pagenabeTaleSQL = " LIMIT ? OFFSET ?";

    private boolean total_records_not_initialized;
    private long total_records;

    public JdbcNativePostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        postRowMapper=new PostRowMapper(this);
        tagRowMapper=new TagRowMapper();
    }

    @Override
    public List<Post> findAll(List<String> searchwords, List<String> tags, int pageNumber, int pageSize) {
        String searchSubstring = searchwords.stream()
                .map(String::toLowerCase)
                .reduce((a, b) -> a + " " + b)
                .orElse("");        // Handle empty stream case
        String tagString = tags.stream()
                .map(a -> tagIlikeSQL+"'"+a+"'")
                .reduce((a, b) -> a + andPrefixSQL + b)
                .orElse("");
        String selectSQL = headOfSelectSQL;
        String whereSQL = "";
        String wherePrefixSQL = initialWherePrefixSQL;
        if (!tagString.isEmpty()) {
            selectSQL = selectSQL+joinFromPartOfSelectSQL;
            whereSQL = wherePrefixSQL+joinWherePartOfSelectSQL+tagString;
            wherePrefixSQL = andPrefixSQL;
        };
        if (!searchSubstring.isEmpty()) {
            whereSQL = whereSQL+wherePrefixSQL+searchIlikeSQL+"'%"+searchSubstring+"%'";
        };
        selectSQL = selectSQL+whereSQL+pagenabeTaleSQL;
        System.out.println(selectSQL + " " + pageSize +" "+pageNumber);
        //jdbcTemplate.query
        total_records_not_initialized=true;
        total_records=0;
        return jdbcTemplate.query(selectSQL,postRowMapper, Integer.valueOf(pageSize), Integer.valueOf(pageSize*(pageNumber-1)));
    }

    @Override
    public boolean existsById(Long id) {
         Integer userCount = jdbcTemplate.queryForObject("select count(*) from posts where id=?", Integer.class, id);
         return userCount != null && userCount > 0;
    }


    @Override
    public Post getById(Long id) {
        return jdbcTemplate.queryForObject(SelectByIdSQL, postRowMapper,id);
    }

    @Override
    public Post save(PostDTO postDTO) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(InsertSQL, new String[]{"id"});
            ps.setString(1, postDTO.title());
            ps.setString(2, postDTO.text());
            return ps;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        this.saveTags(id, postDTO.tags());
        return getById(id);
    }

    @Override
    public Post update(Long id, PostDTO postDTO) {
        jdbcTemplate.update(UpdateByIdSQL,
                postDTO.title(), postDTO.text(), id);
        this.saveTags(id, postDTO.tags());
        return getById(id);
    }

    @Override
    public boolean deleteById(Long id) {
        try {
            jdbcTemplate.update(DeleteByIdSQL, id);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public String getFileNameByPostId(Long id) {
        //тут просится Optional
        return jdbcTemplate.queryForObject(SelectByIdSQL, postRowMapper,id).getImage();
    }

    @Override
    public boolean setFileNameByPostId(Long id, String fileName) {
        jdbcTemplate.update(UpdateImageByIdSQL, fileName, id);
       return true;
    }

    @Override
    public List<String> getTagsByPostId(Long id) { return jdbcTemplate.query(SelectTagsByPostIdSQL,tagRowMapper, id); }

    @Override
    public Long like(Long id) {
        jdbcTemplate.update(IncrementlikesByIdSQL, id);
        return jdbcTemplate.queryForList(SelectlikesByIdSQL, Long.class, id).stream()
                .findFirst()
                .orElse(0L);
    }

    @Override
    public Long getPostsCommentsCountById(Long id) {
        return jdbcTemplate.queryForList(SelectPostsCommentsCountByIdSQL, Long.class, id).stream()
                .findFirst()
                .orElse(0L);
    }

    @Override
    public String getFileSuffix() {
        return jdbcTemplate.queryForList(SelectFileSuffixFromSeqSQL, String.class).stream()
                .findFirst()
                .orElse("");
    }

    public void saveTags (Long id, String[] tags) {
        //удаляем старые тэги
        jdbcTemplate.update(DeleteTagsByPostIdSQL, id);
        //проверка на повторение тэгов в массиве и их сохранение
        Set<Character> distinct = new HashSet<>();
        Stream.of(tags).filter(m -> distinct.add(m.charAt(0)))
                .forEach(s -> saveTag(id,s));
    }

    public Long registerNewTag(String tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(InsertTagSQL, new String[]{"id"});
            ps.setString(1, tag);
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
    }

    public void saveTag (Long postid, String tag) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        List<Long> tagsids = jdbcTemplate.queryForList(SelectTagIdByTagSQL, Long.class,tag);
        long tagid = tagsids.stream().findFirst()
                .orElseGet(() -> registerNewTag(tag));
        jdbcTemplate.update(InsertPostTagSQL, postid, tagid);
    }

}
