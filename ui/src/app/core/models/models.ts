// ─── Enums ────────────────────────────────────────────────────────────────────

export type TipoMidia = 'AUDIO' | 'FOTO' | 'VIDEO' | 'PDF' | 'DOCUMENTO';

export type Tipo = 'ALUNO' | 'PROFESSOR' | 'SECRETARIA' | 'DESATIVADO';

export type Status = 'ON' | 'OFF';

export type StatusFAQ = 'RASCUNHO' | 'PUBLICADO' | 'APAGADO';

export type Prioridade = 'URGENTE' | 'ALTA' | 'MEDIA' | 'BAIXA';

export type TargetType = 'GERAL' | 'ALUNOS' | 'PROFESSORES' | 'TURMAS' | 'TURMA' | 'EX_ALUNOS';

export type Cursos =
  | 'ADMINISTRACAO'
  | 'CONTABILIDADE'
  | 'DESENVOLVIMENTO_DE_SISTEMAS'
  | 'LOGISTICA'
  | 'SERVICOS_JURIDICOS'
  | 'MATUTINO_ADMINISTRACAO_MTEC'
  | 'MATUTINO_CONTABILIDADE_MTEC'
  | 'MATUTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC'
  | 'MATUTINO_LOGISTICA_MTEC'
  | 'MATUTINO_RECURSOS_HUMANOS_MTEC'
  | 'VESPERTINO_ADMINISTRACAO_MTEC'
  | 'VESPERTINO_CONTABILIDADE_MTEC'
  | 'VESPERTINO_DESENVOLVIMENTO_DE_SISTEMAS_MTEC'
  | 'VESPERTINO_LOGISTICA_MTEC'
  | 'VESPERTINO_RECURSOS_HUMANOS_MTEC'
  | 'LOGISTICA_MTEC_N'
  | 'DESENVOLVIMENTO_DE_SISTEMAS_AMS';

// ─── Pagination ───────────────────────────────────────────────────────────────

export interface PageResult<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface SliceResult<T> {
  content: T[];
  page: number;
  size: number;
  hasNext: boolean;
}

// ─── Primitivos reutilizáveis ─────────────────────────────────────────────────

export interface Content {
  content: string;
}

export interface Name {
  name: string;
}

export interface Email {
  email: string;
}

export interface Password {
  password: string;
}

export interface PhoneNumber {
  number: string;
}

export interface Midia {
  tipoMidia: TipoMidia;
  link: string;
}

export interface TargetVO {
  targetType: TargetType;
  targetIds: string[];
}

// ─── Auth ─────────────────────────────────────────────────────────────────────

/** POST /conecta/auth/login */
export interface DTOLogin {
  id: string;
  senha: Password;
}

// ─── Users ────────────────────────────────────────────────────────────────────

/** POST /conecta/management/usuarios e /lote */
export interface DTOCadastro {
  id: string;
  nome: Name;
  email: Email;
  numero: PhoneNumber;
  senha: Password;
  tipo: Tipo;
  turmas: string[];
}

/** GET retorno de alunos/professores em rotas normais */
export interface DTORetornoNormal {
  id: string;
  nome: Name;
  tipo: Tipo;
}

/** GET retorno de usuários na secretaria */
export interface DTORetornoSecretaria {
  id: string;
  nome: Name;
  email: Email;
  numero: PhoneNumber;
  tipo: Tipo;
}

// ─── Turmas ───────────────────────────────────────────────────────────────────

/** GET retorno de turma */
export interface Turma {
  id: string;
  curso: Cursos;
  modulos: number;
  atual: number;
  status: Status;
}

/** POST /conecta/management/turmas — agora só o curso; módulos calculados pelo backend */
export type DTOCadastroTurma = Cursos;

// ─── Mensagens ────────────────────────────────────────────────────────────────

/** GET /conecta/mensagens/contatos */
export interface DTOContatos {
  nome: Name;
  id: string;
}

/** POST /conecta/mensagens/{id} — body enviado */
export interface DTOInfoMessage {
  content: Content | null;
  midia: Midia | null;
}

/** GET /conecta/mensagens/{id} — retorno de mensagem */
export interface DTOReturnMessage {
  nameSender: Name;
  content: Content;
  midia: Midia | null;
}

/** GET /conecta/management/mensagens — retorno secretaria */
export interface DTOReturnMessageSecretaria {
  id: string;
  nomeSender: Name;
  idSender: string;
  nomeReceiver: Name;
  idReceiver: string;
  timestamp: string; // ISO string
  content: Content;
  midia: Midia | null;
}

// ─── FAQs ─────────────────────────────────────────────────────────────────────

/** POST /conecta/faqs/management — body enviado */
export interface DTORegisterFAQ {
  question: string;
  answer: string;
  relevance: Prioridade;
}

/** PATCH /conecta/faqs/management/{id} — body enviado */
export interface DTOUpdateFaq {
  pergunta: string;
  resposta: string;
}

/** GET /conecta/faqs — retorno público */
export interface DTOReturnFAQ {
  question: string;
  answer: string;
}

/** GET /conecta/faqs/management — retorno secretaria */
export interface FAQ {
  id: string;
  authorId: string;
  question: string;
  answer: string;
  statusFAQ: StatusFAQ;
  createdAt: string; // ISO string
  updatedAt: string | null;
  relevance: Prioridade;
  pesoPrioridade: number;
}

// ─── Anúncios ─────────────────────────────────────────────────────────────────

/** POST /conecta/anuncios/management — body enviado */
export interface DTOAnuncio {
  title: Content;
  content: Content;
  midia: Midia | null;
  priority: Prioridade;
  targetType: TargetType;
  targetsId: string[];
}

/** PATCH /conecta/anuncios/management/{id} — body enviado */
export interface DTOAlteraAnuncio {
  title: Content | null;
  content: Content | null;
  midia: Midia | null;
  priority: Prioridade | null;
}

/** GET /conecta/anuncios — retorno público */
export interface DTORetornoAnuncio {
  nome: Name;
  titulo: Content;
  content: Content;
  midia: Midia | null;
  publicacao: string; // ISO string
  edited: boolean;
}

/** GET /conecta/anuncios/management — retorno secretaria */
export interface Statement {
  id: string;
  idSender: string;
  title: Content;
  timestamp: string; // ISO string
  content: Content;
  midia: Midia | null;
  priority: Prioridade;
  edited: boolean;
  status: Status;
  targetVO: TargetVO;
  pesoPrioridade: number;
}

export interface UserData {
  id: string;
  nome: string;
  role: string;
}