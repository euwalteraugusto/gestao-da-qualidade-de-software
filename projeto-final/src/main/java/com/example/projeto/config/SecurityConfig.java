package com.example.projeto.config;

import com.example.projeto.auth.JwtAuthFilter;
import com.example.projeto.auth.JwtProperties;
import com.example.projeto.auth.JwtService;
import com.example.projeto.auth.UsuarioFactory;
import com.example.projeto.auth.UsuariosProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

/**
 * Seguranca com Spring Security 6 (DSL lambda; {@code .csrf().disable()} do slide foi
 * removido no Spring Security 6/7).
 * <p>
 * Duas cadeias:
 * <ol>
 *   <li><b>API</b> ({@code /api/**}, {@code /auth/**}, {@code /actuator/**}): JWT Bearer, sem criar sessao,
 *   respostas 401/403 em JSON. CSRF e ignorado apenas para {@code /auth/**} e para
 *   requisicoes com {@code Authorization: Bearer} (tokens nao sao enviados
 *   automaticamente pelo navegador, logo nao ha CSRF). Requisicoes autenticadas por
 *   sessao (o AJAX da pagina web) continuam protegidas por CSRF.</li>
 *   <li><b>Web</b> (demais rotas): login por formulario e sessao, CSRF ativo.</li>
 * </ol>
 * Autorizacao fina por metodo com {@code @PreAuthorize} (dica da aula 18).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, UsuariosProperties.class})
public class SecurityConfig {

    private static final String[] ROTAS_PUBLICAS_WEB = {
            "/css/**", "/favicon.ico", "/error", "/login",
            "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**"
    };

    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http, JwtService jwtService,
                                              ObjectMapper objectMapper) throws Exception {
        AuthenticationEntryPoint entryPoint = (req, res, ex) ->
                escreverProblema(res, objectMapper, HttpStatus.UNAUTHORIZED, "Nao autenticado",
                        "Token ausente, invalido ou expirado");
        AccessDeniedHandler acessoNegado = (req, res, ex) ->
                escreverProblema(res, objectMapper, HttpStatus.FORBIDDEN, "Acesso negado",
                        "Seu perfil nao tem permissao para esta operacao");

        http
                .securityMatcher("/api/**", "/auth/**", "/actuator/**")
                .csrf(csrf -> csrf.ignoringRequestMatchers(requisicaoComBearer(), req ->
                        req.getRequestURI().startsWith(req.getContextPath() + "/auth/")))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.NEVER))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        // Actuator (aula 19): health/info publicos (health de Heroku/load balancer), resto so ADMIN
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers("/actuator/**").hasRole(UsuarioFactory.ROLE_ADMIN)
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .exceptionHandling(e -> e.authenticationEntryPoint(entryPoint).accessDeniedHandler(acessoNegado))
                .addFilterBefore(new JwtAuthFilter(jwtService, entryPoint), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(ROTAS_PUBLICAS_WEB).permitAll()
                        .requestMatchers("/admin/**").hasRole(UsuarioFactory.ROLE_ADMIN)
                        .anyRequest().authenticated())
                .formLogin(form -> form.defaultSuccessUrl("/produtos", true).permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Delegating: le o prefixo {bcrypt} dos hashes; BCrypt e o padrao para novas senhas.
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService(UsuariosProperties usuarios) {
        return new InMemoryUserDetailsManager(
                UsuarioFactory.criarAdmin(usuarios.admin().username(), usuarios.admin().passwordHash()),
                UsuarioFactory.criarCliente(usuarios.cliente().username(), usuarios.cliente().passwordHash()));
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return new ProviderManager(provider);
    }

    private static RequestMatcher requisicaoComBearer() {
        return (HttpServletRequest req) -> {
            String header = req.getHeader(HttpHeaders.AUTHORIZATION);
            return header != null && header.startsWith("Bearer ");
        };
    }

    private static void escreverProblema(jakarta.servlet.http.HttpServletResponse res, ObjectMapper mapper,
                                         HttpStatus status, String titulo, String detalhe) throws IOException {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detalhe);
        pd.setTitle(titulo);
        res.setStatus(status.value());
        res.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        mapper.writeValue(res.getOutputStream(), pd);
    }
}
