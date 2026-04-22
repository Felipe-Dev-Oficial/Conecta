package com.etec.zl.conecta.Infraestructure.Configuration;

import com.etec.zl.conecta.Application.Mappers.FAQs.FAQMapper;
import com.etec.zl.conecta.Application.Mappers.Messages.MessageMapper;
import com.etec.zl.conecta.Application.Mappers.Statements.StatementMapper;
import com.etec.zl.conecta.Application.Mappers.Turmas.TurmaMapper;
import com.etec.zl.conecta.Application.Mappers.Users.UserMapper;
import com.etec.zl.conecta.Application.Ports.Output.Repositories.*;
import com.etec.zl.conecta.Application.Ports.Output.Services.EmailService;
import com.etec.zl.conecta.Application.Ports.Output.Services.NotificationService;
import com.etec.zl.conecta.Application.Ports.Output.Storage.MidiaStorage;
import com.etec.zl.conecta.Application.Services.Services.FAQs.VerifyIfExistsModifyAndSaveFAQsService;
import com.etec.zl.conecta.Application.Services.Services.Statements.VerifyIfExistsModifyAndSaveStatementsService;
import com.etec.zl.conecta.Application.Services.Services.Users.*;
import com.etec.zl.conecta.Application.UseCases.FAQs.*;
import com.etec.zl.conecta.Application.UseCases.Messages.*;
import com.etec.zl.conecta.Application.UseCases.Midia.UploadMidiaUseCase;
import com.etec.zl.conecta.Application.UseCases.Statements.*;
import com.etec.zl.conecta.Application.UseCases.Turmas.*;
import com.etec.zl.conecta.Application.UseCases.Users.*;
import com.etec.zl.conecta.Infraestructure.Adapters.Output.Gateways.Gateways.EmailServiceAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
class AppConfiguration {

    @Bean
    EmailService emailService(JavaMailSender mailSender) {
        return new EmailServiceAdapter(mailSender);
    }

    @Bean
    UserMapper userMapper() {
        return new UserMapper();
    }

    @Bean
    TurmaMapper turmaMapper() {
        return new TurmaMapper();
    }

    @Bean
    MessageMapper messageMapper() {
        return new MessageMapper();
    }

    @Bean
    FAQMapper faqMapper() {
        return new FAQMapper();
    }

    @Bean
    StatementMapper statementMapper() {
        return new StatementMapper();
    }

    @Bean
    TryGetByUserService tryGetByUserService() {
        return new TryGetByUserService();
    }

    @Bean
    TryGetByUserSecretariaService tryGetByUserSecretariaService(UserMapper mapper) {
        return new TryGetByUserSecretariaService(mapper);
    }

    @Bean
    TryGetUsersService tryGetUsersService(UserMapper mapper) {
        return new TryGetUsersService(mapper);
    }

    @Bean
    TryGetUsersSecretariaService tryGetUsersSecretariaService(UserMapper mapper) {
        return new TryGetUsersSecretariaService(mapper);
    }

    @Bean
    TrySaveUserService trySaveUserService(UserRepository userRepository, UserMapper mapper, TurmaRepository turmaRepository) {
        return new TrySaveUserService(userRepository, mapper, turmaRepository);
    }

    @Bean
    VerifyIfExistsModifyAndSaveUsersService verifyIfExistsModifyAndSaveUsersService(UserRepository userRepository) {
        return new VerifyIfExistsModifyAndSaveUsersService(userRepository);
    }

    @Bean
    VerifyIfExistsModifyAndSaveStatementsService verifyIfExistsModifyAndSaveStatementsService(StatementRepository statementRepository) {
        return new VerifyIfExistsModifyAndSaveStatementsService(statementRepository);
    }

    @Bean
    VerifyIfExistsModifyAndSaveFAQsService verifyIfExistsModifyAndSaveFAQsService(FAQRepository faqRepository) {
        return new VerifyIfExistsModifyAndSaveFAQsService(faqRepository);
    }

    @Bean
    StartChangeService startChangeService(UserRepository userRepository, EmailService emailService) {
        return new StartChangeService(userRepository, emailService);
    }

    @Bean
    AlterarEmailUseCase alterarEmailUseCase(VerifyIfExistsModifyAndSaveUsersService service) {
        return new AlterarEmailUseCase(service);
    }

    @Bean
    AlterarSenhaUseCase alterarSenhaUseCase(VerifyIfExistsModifyAndSaveUsersService service) {
        return new AlterarSenhaUseCase(service);
    }

    @Bean
    AlterarTipoUseCase alterarTipoUseCase(UserRepository userRepository, VerifyIfExistsModifyAndSaveUsersService service) {
        return new AlterarTipoUseCase(userRepository, service);
    }

    @Bean
    SalvarUsuarioUseCase salvarUsuarioUseCase(TrySaveUserService service, UserRepository userRepository, TryGetByUserService tryGetByUserService) {
        return new SalvarUsuarioUseCase(service, userRepository, tryGetByUserService);
    }

    @Bean
    NovosUsuariosUseCase novosUsuariosUseCase(TrySaveUserService service) {
        return new NovosUsuariosUseCase(service);
    }

    @Bean
    DeletarUsuarioUseCase deletarUsuarioUseCase(VerifyIfExistsModifyAndSaveUsersService verifyIfExistsModifyAndSaveUsersService) {
        return new DeletarUsuarioUseCase(verifyIfExistsModifyAndSaveUsersService);
    }

    @Bean
    SolicitarAlteracaoEmailUseCase solicitarAlteracaoEmailUseCase(UserRepository userRepository, StartChangeService service) {
        return new SolicitarAlteracaoEmailUseCase(userRepository, service);
    }

    @Bean
    SolicitarAlteracaoSenhaUseCase solicitarAlteracaoSenhaUseCase(UserRepository userRepository, StartChangeService service) {
        return new SolicitarAlteracaoSenhaUseCase(userRepository, service);
    }

    @Bean
    ProfessorBuscaPorIdUseCase professorBuscaPorIdUseCase(UserRepository userRepository, TryGetByUserService service, UserMapper userMapper) {
        return new ProfessorBuscaPorIdUseCase(userRepository, service, userMapper);
    }

    @Bean
    ProfessorListagemUseCase professorListagemUseCase(UserRepository userRepository, TryGetUsersService service) {
        return new ProfessorListagemUseCase(userRepository, service);
    }

    @Bean
    ProfessorListagemPorNomeUseCase professorListagemPorNomeUseCase(UserRepository userRepository, TryGetUsersService service) {
        return new ProfessorListagemPorNomeUseCase(userRepository, service);
    }

    @Bean
    ProfessorListagemPorTurmaUseCase professorListagemPorTurmaUseCase(UserRepository userRepository, TryGetUsersService service) {
        return new ProfessorListagemPorTurmaUseCase(userRepository, service);
    }

    @Bean
    AlunoBuscaProfessorPorIdUseCase alunoBuscaProfessorPorIdUseCase(UserRepository userRepository, TryGetByUserService service, UserMapper userMapper) {
        return new AlunoBuscaProfessorPorIdUseCase(userRepository, service, userMapper);
    }

    @Bean
    AlunoListagemProfessorUseCase alunoListagemProfessorUseCase(UserRepository userRepository, TryGetUsersService service) {
        return new AlunoListagemProfessorUseCase(userRepository, service);
    }

    @Bean
    SecretariaBuscaPorIdUseCase secretariaBuscaPorIdUseCase(UserRepository userRepository, TryGetByUserSecretariaService service) {
        return new SecretariaBuscaPorIdUseCase(userRepository, service);
    }

    @Bean
    SecretariaListagemUseCase secretariaListagemUseCase(UserRepository userRepository, TryGetUsersSecretariaService service) {
        return new SecretariaListagemUseCase(userRepository, service);
    }

    @Bean
    SecretariaListagemPorNomeUseCase secretariaListagemPorNomeUseCase(UserRepository userRepository, TryGetUsersSecretariaService service) {
        return new SecretariaListagemPorNomeUseCase(userRepository, service);
    }

    @Bean
    SecretariaListagemPorTurmaUseCase secretariaListagemPorTurmaUseCase(UserRepository userRepository, TryGetUsersSecretariaService service) {
        return new SecretariaListagemPorTurmaUseCase(userRepository, service);
    }

    @Bean
    SecretariaListagemPorCursantesUseCase secretariaListagemPorCursantesUseCase(UserRepository userRepository, TryGetUsersSecretariaService service) {
        return new SecretariaListagemPorCursantesUseCase(userRepository, service);
    }

    @Bean
    SecretariaListagemPorFuncionariosUseCase secretariaListagemPorFuncionariosUseCase(UserRepository userRepository, TryGetUsersSecretariaService service) {
        return new SecretariaListagemPorFuncionariosUseCase(userRepository, service);
    }

    @Bean
    RetornarContatosUseCase retornarContatosUseCase(MessageRepository messageRepository, UserRepository userRepository, MessageMapper messageMapper) {
        return new RetornarContatosUseCase(messageRepository, userRepository, messageMapper);
    }

    @Bean
    EnviarMensagemUseCase enviarMensagemUseCase(MessageRepository messageRepository, UserRepository userRepository, MessageMapper mapper, NotificationService  notificationService) {
        return new EnviarMensagemUseCase(messageRepository, userRepository, mapper, notificationService);
    }

    @Bean
    LerMensagensUseCase lerMensagensUseCase(MessageRepository messageRepository, MessageMapper mapper, UserRepository userRepository) {
        return new LerMensagensUseCase(messageRepository, mapper, userRepository);
    }

    @Bean
    LerMensagensSecretariaUseCase lerMensagensSecretariaUseCase(MessageRepository messageRepository, UserRepository userRepository, MessageMapper mapper) {
        return new LerMensagensSecretariaUseCase(messageRepository, userRepository, mapper);
    }

    @Bean
    EscreverFAQUseCase escreverFAQUseCase(FAQRepository faqRepository, UserRepository userRepository, FAQMapper mapper) {
        return new EscreverFAQUseCase(faqRepository, userRepository, mapper);
    }

    @Bean
    AlterarFAQUseCase alterarFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        return new AlterarFAQUseCase(service);
    }

    @Bean
    ApagarFAQUseCase apagarFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        return new ApagarFAQUseCase(service);
    }

    @Bean
    PublicarFAQUseCase rFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        return new PublicarFAQUseCase(service);
    }

    @Bean
    AumentarPrioridadeFAQUseCase aumentarPrioridadeFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        return new AumentarPrioridadeFAQUseCase(service);
    }

    @Bean
    DiminuirPrioridadeFAQUseCase diminuirPrioridadeFAQUseCase(VerifyIfExistsModifyAndSaveFAQsService service) {
        return new DiminuirPrioridadeFAQUseCase(service);
    }

    @Bean
    LerFAQUseCase lerFAQUseCase(FAQRepository faqRepository, FAQMapper mapper) {
        return new LerFAQUseCase(faqRepository, mapper);
    }

    @Bean
    LerFAQsSecretariaUseCase lerFAQsSecretariaUseCase(FAQRepository faqRepository) {
        return new LerFAQsSecretariaUseCase(faqRepository);
    }

    @Bean
    GerarAnuncioUseCase gerarAnuncioUseCase(StatementRepository statementRepository, StatementMapper mapper, UserRepository userRepository, NotificationService notificationService) {
        return new GerarAnuncioUseCase(statementRepository, mapper, userRepository, notificationService);
    }

    @Bean
    AlterarAnuncioUseCase alterarAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service) {
        return new AlterarAnuncioUseCase(service);
    }

    @Bean
    ApagarAnuncioUseCase apagarAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service) {
        return new ApagarAnuncioUseCase(service);
    }

    @Bean
    ElevarPrioridadeAnuncioUseCase elevarPrioridadeAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service) {
        return new ElevarPrioridadeAnuncioUseCase(service);
    }

    @Bean
    ReduzirPrioridadeAnuncioUseCase reduzirPrioridadeAnuncioUseCase(VerifyIfExistsModifyAndSaveStatementsService service) {
        return new ReduzirPrioridadeAnuncioUseCase(service);
    }

    @Bean
    LerAnuncioUseCase lerAnuncioUseCase(StatementRepository statementRepository, UserRepository userRepository, StatementMapper mapper, TryGetByUserService userService) {
        return new LerAnuncioUseCase(statementRepository, userRepository, mapper, userService);
    }

    @Bean
    LerAnuncioSecretariaUseCase lerAnuncioSecretariaUseCase(StatementRepository statementRepository, UserRepository userRepository, StatementMapper mapper, TryGetByUserService userService) {
        return new LerAnuncioSecretariaUseCase(statementRepository, userRepository, mapper, userService);
    }
    @Bean
    public ListarTodasAsTurmasAtuaisUseCase listarTodasAsTurmasAtuaisUseCase(TurmaRepository turmaRepository) {
        return new ListarTodasAsTurmasAtuaisUseCase(turmaRepository);
    }

    @Bean
    public ListarTodasAsTurmasUseCase listarTodasAsTurmasUseCase(TurmaRepository turmaRepository) {
        return new ListarTodasAsTurmasUseCase(turmaRepository);
    }

    @Bean
    public ListarTurmasPorCursoAtuaisUseCase listarTurmasPorCursoAtuaisUseCase(TurmaRepository turmaRepository) {
        return new ListarTurmasPorCursoAtuaisUseCase(turmaRepository);
    }

    @Bean
    public ListarTurmasPorCursoUseCase listarTurmasPorCursoUseCase(TurmaRepository turmaRepository) {
        return new ListarTurmasPorCursoUseCase(turmaRepository);
    }

    @Bean
    public NovasTurmasUseCase novasTurmasUseCase(TurmaRepository turmaRepository, TurmaMapper turmaMapper) {
        return new NovasTurmasUseCase(turmaRepository, turmaMapper);
    }

    @Bean
    public PassaModuloUseCase passaModuloUseCase(TurmaRepository turmaRepository) {
        return new PassaModuloUseCase(turmaRepository);
    }

    @Bean
    public EncontrarTurmaPorIdUseCase encontrarTurmaPorIdUseCase(TurmaRepository turmaRepository) {
        return new EncontrarTurmaPorIdUseCase(turmaRepository);
    }

    @Bean
    public RetornarSecretariaUseCase retornarSecretariaUseCase(UserRepository userRepository, TryGetUsersService tryGetUsersService) {
        return new RetornarSecretariaUseCase(userRepository, tryGetUsersService);
    }

    @Bean
    public UploadMidiaUseCase uploadMidiaUseCase(MidiaStorage midiaStorage) {
        return new UploadMidiaUseCase(midiaStorage);
    }

    @Bean
    public LerAnunciosGeraisUseCase lerAnunciosGeraisUseCase(StatementMapper statementRepository, TryGetByUserService userService, StatementRepository statementMapper, UserRepository userRepository){
        return new LerAnunciosGeraisUseCase(statementRepository, userService, statementMapper, userRepository);
    }

    @Bean
    public VincularNotificadorUseCase vincularTokenNotificadorUseCase(UserRepository userRepository, TryGetByUserService tryGetByUserService) {
        return new VincularNotificadorUseCase(userRepository, tryGetByUserService);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}