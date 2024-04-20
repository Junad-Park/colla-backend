package one.colla.common.security.filter;

import static one.colla.global.exception.ExceptionCode.*;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import one.colla.common.security.jwt.JwtClaims;
import one.colla.common.security.jwt.JwtProvider;
import one.colla.common.security.jwt.access.AccessTokenClaimKeys;
import one.colla.global.exception.CommonException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final UserDetailsService userDetailService;
	private final JwtProvider accessTokenProvider;

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
		@NonNull FilterChain filterChain) throws
		ServletException, IOException {
		if (isAnonymousRequest(request)) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken = resolveAccessToken(request, response);

		UserDetails userDetails = getUserDetails(accessToken);
		authenticateUser(userDetails, request);
		filterChain.doFilter(request, response);
	}

	private boolean isAnonymousRequest(HttpServletRequest request) {
		String accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);

		return accessToken == null;
	}

	private String resolveAccessToken(HttpServletRequest request, HttpServletResponse response) {
		String authenticationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		String token = accessTokenProvider.resolveToken(authenticationHeader);

		if (!StringUtils.hasText(token)) {
			throw new CommonException(EMPTY_ACCESS_TOKEN);
		}

		// TODO: 로그아웃 시 Forbidden 필요
		// if (forbiddenTokenService.isForbidden(token)) {
		// 	handleAuthException(JwtErrorCode.FORBIDDEN_ACCESS_TOKEN);
		// }

		return token;
	}

	private UserDetails getUserDetails(String accessToken) {
		JwtClaims claims = accessTokenProvider.getJwtClaimsFromToken(accessToken);
		String userId = (String)claims.getClaims().get(AccessTokenClaimKeys.USER_ID.getValue());

		return userDetailService.loadUserByUsername(userId);
	}

	private void authenticateUser(UserDetails userDetails, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
			userDetails, null, userDetails.getAuthorities()
		);

		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
