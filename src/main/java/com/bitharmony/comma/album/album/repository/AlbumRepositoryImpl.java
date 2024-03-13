package com.bitharmony.comma.album.album.repository;

import java.security.Principal;
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
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
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

            conditions.stream().reduce(BooleanExpression::or).ifPresent(builder::and);

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

	@Override
	public Page<Album> streamingTop10Albums(Pageable pageable) {
		QAlbum album = QAlbum.album;
		QMember member = QMember.member;

		JPAQuery<Album> query = jpaQueryFactory
			.select(album)
			.from(album)
			.leftJoin(album.member, member)
			.orderBy(album.streamingCounts.size().desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		JPAQuery<Long> totalQuery = jpaQueryFactory
			.select(album.count())
			.from(album)
			.leftJoin(album.member, member);
		return PageableExecutionUtils.getPage(query.fetch(), pageable, totalQuery::fetchOne);
	}

	@Override
	public Page<Album> musicRecommendation10Albums(Principal principal, Pageable pageable) {
		//비회원도 추천을 해야 하는가? -> 어짜피 로그인 해야 들을 수 있을꺼면..
		//테스트 필요.. 지금은 앨범 10개가 안되어서.. 랜덤으로 나오긴 함

		QAlbum album = QAlbum.album;
		QMember member = QMember.member;

		NumberExpression<Double> rand = Expressions.numberTemplate(Double.class, "rand()");
		OrderSpecifier<Double> orderSpecifier = new OrderSpecifier<>(Order.ASC, rand);

		if(principal != null) {
			// 로그인한 사용자의 경우, 사용자가 좋아하는 앨범의 장르를 기반으로 앨범을 추천합니다.
			List<String> likedGenres = jpaQueryFactory
				.select(album.genre)
				.from(album)
				.where(album.albumLikes.contains(member))
				.fetch();

			JPAQuery<Album> query = jpaQueryFactory
				.select(album)
				.from(album)
				.where(album.genre.in(likedGenres))
				.orderBy(orderSpecifier)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize());

			List<Album> recommendedAlbums = query.fetch();

			// 선택된 앨범의 개수가 10개 미만인 경우, 장르에 상관없이 랜덤으로 앨범을 추가로 선택하여 10개를 채웁니다.
			if(recommendedAlbums.size() < 10) {
				long remaining = 10 - recommendedAlbums.size();
				List<Album> randomAlbums = jpaQueryFactory
					.select(album)
					.from(album)
					.orderBy(orderSpecifier)
					.limit(remaining)
					.fetch();
				recommendedAlbums.addAll(randomAlbums);
			}

			JPAQuery<Long> totalQuery = jpaQueryFactory
				.select(album.count())
				.from(album)
				.where(album.genre.in(likedGenres));

			return PageableExecutionUtils.getPage(recommendedAlbums, pageable, totalQuery::fetchOne);
		} else {
			// 로그인하지 않은 사용자의 경우, 모든 앨범 중에서 랜덤으로 앨범을 추천합니다.
			JPAQuery<Album> query = jpaQueryFactory
				.select(album)
				.from(album)
				.leftJoin(album.member, member)
				.orderBy(orderSpecifier)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize());

			JPAQuery<Long> totalQuery = jpaQueryFactory
				.select(album.count())
				.from(album)
				.leftJoin(album.member, member);
			return PageableExecutionUtils.getPage(query.fetch(), pageable, totalQuery::fetchOne);
		}
	}
}