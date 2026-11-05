import { useEffect, useRef, useState, type FormEvent } from 'react';
import { attributeKeys, calculatePlan, labels, type Attributes, type PlanRequest, type PlanResponse } from './api';

const initial: PlanRequest = {
  rulesetId: 'attributes-2.0-v1', level: 23, targetLevel: 40, phantomLiberty: false,
  resetAvailable: false, allowReset: false,
  attributes: { body: 10, reflexes: 10, technicalAbility: 9, intelligence: 8, cool: 7 },
  targetAttributes: { body: 15, reflexes: 20, technicalAbility: 9, intelligence: 3, cool: 3 },
};
const presets: { name: string; description: string; attributes: Attributes }[] = [
  { name: 'Mobilidade', description: 'Ênfase em reflexos', attributes: { body: 15, reflexes: 20, technicalAbility: 9, intelligence: 3, cool: 3 } },
  { name: 'Hacking', description: 'Ênfase em inteligência', attributes: { body: 9, reflexes: 3, technicalAbility: 15, intelligence: 20, cool: 3 } },
  { name: 'Resistência', description: 'Ênfase em corpo', attributes: { body: 20, reflexes: 9, technicalAbility: 15, intelligence: 3, cool: 3 } },
];
const statusLabels = { REACHABLE: 'Você tem um caminho.', REQUIRES_RESET: 'Uma nova rota exige reset.', UNREACHABLE: 'Ajuste o destino.' };

const art: Record<string, string> = { body: 'body-d1fe68db.png', reflexes: 'reflexes-23a0a79d.png', technicalAbility: 'tech-2d71880a.png', intelligence: 'intelligence-b811d04f.png', cool: 'cool-99bb085f.png' };
function AttributeBoard({ title, values, max, level, onChange }: { title: string; values: Attributes; max: number; level: number; onChange: (value: Attributes) => void }) {
  return <div className="board-viewport"><fieldset className="attribute-board" aria-label={title}><legend className="sr-only">{title}</legend>
    {attributeKeys.map(key => <div className={`attribute-tile tile-${key}`} key={key}>
      <div className="tile-face"><label htmlFor={`attr-${key}`}>{labels[key]}</label><div className="tile-stat"><img src={`/planner-assets/${art[key]}`} alt="" /><input id={`attr-${key}`} aria-label={`${title}: ${labels[key]}`} type="number" min={3} max={max} step={1} required value={Number.isFinite(values[key]) ? values[key] : ''} onChange={e => onChange({ ...values, [key]: e.currentTarget.valueAsNumber })} /></div></div>
      <div className="tile-controls"><button type="button" aria-label={`Diminuir ${labels[key]} em ${title}`} disabled={!Number.isFinite(values[key]) || values[key] <= 3} onClick={() => onChange({ ...values, [key]: values[key] - 1 })}>−</button><button type="button" aria-label={`Aumentar ${labels[key]} em ${title}`} disabled={!Number.isFinite(values[key]) || values[key] >= max} onClick={() => onChange({ ...values, [key]: values[key] + 1 })}>+</button></div>
    </div>)}
    <div className="board-level"><strong>{Number.isFinite(level) ? level : '—'}</strong><span>NÍVEL</span></div>
    <div className="relic-tile" aria-label="Relic não disponível nesta prévia"><span>RELIC</span><img src="/planner-assets/relic-b1f6f8ac.png" alt="" /><small>FORA DESTA PRÉVIA</small></div>
  </fieldset></div>;
}

export default function App() {
  const [form, setForm] = useState<PlanRequest>(initial);
  const [editing, setEditing] = useState<'current' | 'target'>('current');
  const [result, setResult] = useState<PlanResponse | null>(null);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const active = useRef<AbortController | null>(null);
  useEffect(() => () => active.current?.abort(), []);
  function update(changes: Partial<PlanRequest>) {
    active.current?.abort(); active.current = null;
    setLoading(false); setResult(null); setError('');
    setForm(previous => ({ ...previous, ...changes }));
  }
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); active.current?.abort();
    const controller = new AbortController(); active.current = controller;
    setLoading(true); setResult(null); setError('');
    try {
      const plan = await calculatePlan(form, controller.signal);
      if (active.current === controller) setResult(plan);
    } catch (problem) {
      if (active.current === controller && !controller.signal.aborted) setError(problem instanceof Error ? problem.message : 'Não foi possível calcular a rota.');
    } finally { if (active.current === controller) setLoading(false); }
  }
  const resetDisabled = form.level === 1;
  return <div className="shell">
    <header className="topbar"><a className="brand" href="#main" aria-label="ChromeLab, início">Chrome<span>Lab</span><small>BUILD NAVIGATOR</small></a><div className="header-meta"><span>CYBERPUNK 2077</span><span className="build-tag">PRÉVIA / 001</span></div></header>
    <main id="main">
      <div className="page-title"><div><p className="eyebrow">BUILD RESCUE / SMART PROGRESSION</p><h1>PERSONAGEM</h1></div><p className="scope-note">Prévia experimental de atributos<br /><span>Regras numéricas aguardam validação no jogo.</span></p></div>
      <form onSubmit={submit}>
        <div className="character-workspace">
          <aside className="character-sidebar" aria-label="Configurações da progressão">
            <h2>PARÂMETROS DA ROTA</h2>
            <label className="level-field">Nível atual<input type="number" min={1} max={form.phantomLiberty ? 60 : 50} step={1} required value={Number.isFinite(form.level) ? form.level : ''} onChange={e => { const level = e.currentTarget.valueAsNumber; update({ level, ...(level === 1 ? { resetAvailable: false, allowReset: false } : {}) }); }} /></label>
            <label className="level-field">Nível alvo<input type="number" min={Number.isFinite(form.level) ? form.level : 1} max={form.phantomLiberty ? 60 : 50} step={1} required value={Number.isFinite(form.targetLevel) ? form.targetLevel : ''} onChange={e => update({ targetLevel: e.currentTarget.valueAsNumber })} /></label>
            <label className="check"><input type="checkbox" checked={form.phantomLiberty} onChange={e => update({ phantomLiberty: e.target.checked })}/><span>Phantom Liberty<small>Progressão até o nível 60.</small></span></label>
            <div className="sidebar-section"><h2>OBJETIVOS RÁPIDOS</h2><div className="presets">{presets.map(preset => <button type="button" key={preset.name} title={preset.description} onClick={() => { update({ targetAttributes: { ...preset.attributes } }); setEditing('target'); }}>{preset.name}<span aria-hidden="true">↗</span></button>)}</div><p className="hint">Sugestões editoriais de atributos. Os objetivos são mínimos desejados.</p></div>
            <div className="sidebar-section reset-options"><h2>REDEFINIÇÃO DE ATRIBUTOS</h2><label className="check"><input type="checkbox" disabled={resetDisabled} checked={form.resetAvailable} onChange={e => update({ resetAvailable: e.target.checked, allowReset: e.target.checked ? form.allowReset : false })}/><span>Ainda tenho o reset disponível</span></label><label className="check"><input type="checkbox" disabled={!form.resetAvailable || resetDisabled} checked={form.allowReset} onChange={e => update({ allowReset: e.target.checked })}/><span>Permitir simulação com reset<small>Nenhuma alteração é feita no jogo.</small></span></label>{resetDisabled && <p className="hint">Reset indisponível nesta prévia no nível 1.</p>}</div>
          </aside>
          <section className="character-stage" aria-label="Distribuição de atributos">
            <div className="mode-tabs" role="tablist" aria-label="Editar distribuição" onKeyDown={event => { if (['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) { event.preventDefault(); const next = event.key === 'Home' ? 'current' : event.key === 'End' ? 'target' : editing === 'current' ? 'target' : 'current'; setEditing(next); document.getElementById(`${next}-tab`)?.focus(); } }}><button id="current-tab" type="button" role="tab" tabIndex={editing === 'current' ? 0 : -1} aria-selected={editing === 'current'} aria-controls="attribute-panel" onClick={() => setEditing('current')}>01 / PERSONAGEM ATUAL</button><button id="target-tab" type="button" role="tab" tabIndex={editing === 'target' ? 0 : -1} aria-selected={editing === 'target'} aria-controls="attribute-panel" onClick={() => setEditing('target')}>02 / OBJETIVO</button></div>
            <p className="stage-hint">{editing === 'current' ? 'REPRODUZA OS ATRIBUTOS DO SEU PERSONAGEM' : 'DEFINA OS MÍNIMOS QUE DESEJA ALCANÇAR'}</p>
            <div id="attribute-panel" role="tabpanel" aria-labelledby={editing === 'current' ? 'current-tab' : 'target-tab'}><AttributeBoard title={editing === 'current' ? 'Atributos atuais' : 'Objetivo mínimo'} values={editing === 'current' ? form.attributes : form.targetAttributes} max={editing === 'current' && form.level === 1 ? 6 : 20} level={editing === 'current' ? form.level : form.targetLevel} onChange={values => update(editing === 'current' ? { attributes: values } : { targetAttributes: values })}/></div>
          </section>
        </div>
        <div className="command-bar"><div className="command-status"><span className="status-dot" />SISTEMA DE ATRIBUTOS<small>Perks, armas e cyberware ainda não são avaliados.</small></div><button className="primary" disabled={loading} type="submit">{loading ? 'Calculando rota…' : 'Traçar minha rota'}<span aria-hidden="true">↗</span></button></div>
      </form>
      <section className="results" aria-live="polite" aria-busy={loading}>
        {error && <div role="alert" className="error"><strong>Não conseguimos traçar sua rota.</strong><p>{error}</p></div>}
        {loading && <p className="loading">Analisando seu ponto de partida e os mínimos desejados…</p>}
        {result && <div className={`result-panel ${result.status.toLowerCase()}`}><div className="result-heading"><div><p className="eyebrow">03 / SUA ROTA</p><h2>{statusLabels[result.status]}</h2></div><span className="result-badge">{result.usesReset ? 'COM RESET' : result.status === 'UNREACHABLE' ? 'OBJETIVO INVIÁVEL' : 'SEM RESET'}</span></div>
          <div className="metrics"><div><span>Pontos necessários</span><strong>{result.pointsNeeded}</strong></div><div><span>Disponíveis até o alvo</span><strong>{result.pointsAvailable}</strong></div><div><span>{result.status === 'UNREACHABLE' ? 'Pontos que faltam' : 'Objetivo no nível'}</span><strong>{result.status === 'UNREACHABLE' ? result.shortfall : result.earliestLevel ?? '—'}</strong></div></div>
          <ul className="explanations">{result.explanations.map((explanation, index) => <li key={index}>{explanation}</li>)}</ul>
          {result.usesReset && <p className="reset-warning">Esta rota parte de uma redistribuição de atributos. Confira a disponibilidade do reset no jogo antes de seguir.</p>}
          {result.status !== 'UNREACHABLE' && <><h3>Atributos ao atingir o objetivo</h3><dl className="final-attributes">{attributeKeys.map(key => <div key={key}><dt>{labels[key]}</dt><dd>{result.finalAttributes[key]}</dd></div>)}</dl>
          {result.steps.length > 0 ? <><h3>Seu próximo investimento, nível a nível</h3><p className="hint">Ordem fixa por atributo nesta prévia. Não representa uma ordem otimizada de combate.</p><ol className="timeline">{result.steps.map((step, index) => <li key={`${index}-${step.attribute}`}><span className="step-level">NÍVEL {step.level}</span><span>{labels[step.attribute]}</span><strong>{step.from} <span aria-hidden="true">→</span><span className="sr-only">para</span> {step.to}</strong></li>)}</ol></> : <p>Seus atributos já atendem aos mínimos. Nenhum ponto adicional é necessário.</p>}</>}
          <p className="result-footnote">Somente atributos • {result.rulesetId} • Saldo atual informado pelo motor: {result.unspentNow}</p>
        </div>}
        {!loading && !error && !result && <div className="empty-state"><span aria-hidden="true">↗</span><div><h2>O próximo capítulo começa aqui.</h2><p>Ajuste os valores acima e trace sua primeira rota.</p></div></div>}
      </section>
    </main><footer><span>CHROMELAB / EXPERIMENTO INDEPENDENTE</span><p>Projeto de fã, sem afiliação à CD PROJEKT RED. Dados desta sessão não são salvos.</p></footer>
  </div>;
}
