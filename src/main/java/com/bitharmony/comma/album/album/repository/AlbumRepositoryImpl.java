package com.bitharmony.comma.album.album.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import com.bitharmony.comma.album.album.entity.Album;
import com.bitharmony.comma.album.album.entity.QAlbum;
import com.bitharmony.comma.member.entity.QMember;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AlbumRepositoryImpl implements AlbumRepositoryCustom{
	final JPAQueryFactory jpaQueryFactory;
	@Override
	public Page<Album> search(List<String> kwTypes, String kw, Pageable pageable) {

		BooleanBuilder builder = new BooleanBuilder();

		QMember member = new QMember("member");
		QAlbum album = new QAlbum("album");

		if (!kw.isBlank()) {
			List<BooleanExpression> conditions = new ArrayList<>();

			if (kwTypes.contains("memberNickname")) {
				conditions.add(album.member.nickname.containsIgnoreCase(kw));
			}

			if (kwTypes.contains("albumGenre")) {
				conditions.add(album.genre.containsIgnoreCase(kw));
			}

			if (kwTypes.contains("albumName")) {
				conditions.add(album.albumname.containsIgnoreCase(kw));
			}

			if (kwTypes.contains("freeAlbum")) {
				conditions.add(album.price.eq(0));
			}

			if (kwTypes.contains("paidAlbum")) {
				conditions.add(album.price.gt(0));
			}

			BooleanExpression combinedConditions = conditions.stream().reduce(BooleanExpression::or).orElse(null);

			if (combinedConditions != null) {
				builder.and(combinedConditions);
			}
		}


		JPAQuery<Album> albumsQuery = jpaQueryFactory
			.selectDistinct(album)
			.from(album)
			.leftJoin(album.member, member)
			.where(builder);


		for (Sort.Order o : pageable.getSort()) {
			PathBuilder pathBuilder = new PathBuilder(album.getType(), album.getMetadata());
			albumsQuery.orderBy(new OrderSpecifier(o.isAscending() ? Order.ASC : Order.DESC, pathBuilder.get(o.getProperty())));
		}

		albumsQuery.offset(pageable.getOffset()).limit(pageable.getPageSize());


		JPAQuery<Long> totalQuery = jpaQueryFactory
			.select(album.countDistinct())
			.from(album)
			.leftJoin(album.member, member)
			.where(builder);

		return PageableExecutionUtils.getPage(albumsQuery.fetch(), pageable, totalQuery::fetchOne);
	}
}