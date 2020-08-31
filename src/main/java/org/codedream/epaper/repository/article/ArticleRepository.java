package org.codedream.epaper.repository.article;

import io.swagger.models.auth.In;
import org.codedream.epaper.model.article.Article;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Integer> {

}
