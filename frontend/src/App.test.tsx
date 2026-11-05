import { cleanup, fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import App from './App';
import type { PlanResponse } from './api';

const plan: PlanResponse = {
  rulesetId: 'attributes-2.0-v1', status: 'REACHABLE', usesReset: false, unspentNow: 0,
  pointsNeeded: 15, pointsAvailable: 17, shortfall: 0, earliestLevel: 38,
  finalAttributes: { body: 15, reflexes: 20, technicalAbility: 9, intelligence: 8, cool: 7 },
  steps: [{ level: 24, attribute: 'body', from: 10, to: 11 }], explanations: ['Rota calculada pelo motor.'],
};
afterEach(() => { cleanup(); vi.unstubAllGlobals(); });
describe('Build Rescue', () => {
  it('edita atual e objetivo independentemente pelos controles do painel', () => {
    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: 'Aumentar Corpo em Atributos atuais' }));
    expect(screen.getByRole('spinbutton', { name: 'Atributos atuais: Corpo' })).toHaveValue(11);
    fireEvent.click(screen.getByRole('tab', { name: '02 / OBJETIVO' }));
    expect(screen.getByRole('spinbutton', { name: 'Objetivo mínimo: Corpo' })).toHaveValue(15);
    fireEvent.click(screen.getByRole('button', { name: 'Diminuir Corpo em Objetivo mínimo' }));
    fireEvent.click(screen.getByRole('tab', { name: '01 / PERSONAGEM ATUAL' }));
    expect(screen.getByRole('spinbutton', { name: 'Atributos atuais: Corpo' })).toHaveValue(11);
    fireEvent.keyDown(screen.getByRole('tab', { name: '01 / PERSONAGEM ATUAL' }), { key: 'ArrowRight' });
    expect(screen.getByRole('tab', { name: '02 / OBJETIVO' })).toHaveFocus();
    expect(screen.getByRole('spinbutton', { name: 'Objetivo mínimo: Corpo' })).toHaveValue(14);
  });
  it('limita os controles de ponto e abre o objetivo ao escolher sugestão', () => {
    render(<App />);
    fireEvent.change(screen.getByRole('spinbutton', { name: 'Atributos atuais: Corpo' }), { target: { value: '3' } });
    expect(screen.getByRole('button', { name: 'Diminuir Corpo em Atributos atuais' })).toBeDisabled();
    fireEvent.change(screen.getByRole('spinbutton', { name: 'Atributos atuais: Corpo' }), { target: { value: '20' } });
    expect(screen.getByRole('button', { name: 'Aumentar Corpo em Atributos atuais' })).toBeDisabled();
    fireEvent.click(screen.getByRole('button', { name: /Hacking/ }));
    expect(screen.getByRole('tab', { name: '02 / OBJETIVO' })).toHaveAttribute('aria-selected', 'true');
    expect(screen.getByRole('spinbutton', { name: 'Objetivo mínimo: Inteligência' })).toHaveValue(20);
  });
  it('envia o contrato à API e exibe o resultado real', async () => {
    const fetchMock = vi.fn().mockResolvedValue({ ok: true, json: async () => plan });
    vi.stubGlobal('fetch', fetchMock); render(<App />);
    fireEvent.click(screen.getByRole('button', { name: /Traçar minha rota/ }));
    expect(await screen.findByText('Você tem um caminho.')).toBeInTheDocument();
    expect(screen.getByText('Rota calculada pelo motor.')).toBeInTheDocument();
    const [url, options] = fetchMock.mock.calls[0];
    expect(url).toBe('/api/v1/plans');
    expect(JSON.parse(options.body)).toMatchObject({ level: 23, targetLevel: 40, allowReset: false, rulesetId: 'attributes-2.0-v1' });
    fireEvent.change(screen.getByRole('spinbutton', { name: 'Nível alvo' }), { target: { value: '41' } });
    expect(screen.queryByText('Você tem um caminho.')).not.toBeInTheDocument();
  });
  it('descarta respostas antigas quando os dados mudam durante a requisição', async () => {
    let complete!: (value: unknown) => void;
    vi.stubGlobal('fetch', vi.fn().mockImplementation(() => new Promise(resolve => { complete = resolve; })));
    render(<App />); fireEvent.click(screen.getByRole('button', { name: /Traçar minha rota/ }));
    fireEvent.change(screen.getByRole('spinbutton', { name: 'Nível alvo' }), { target: { value: '41' } });
    complete({ ok: true, json: async () => plan });
    await waitFor(() => expect(screen.getByRole('button', { name: /Traçar minha rota/ })).not.toBeDisabled());
    expect(screen.queryByText('Você tem um caminho.')).not.toBeInTheDocument();
  });
  it('não fabrica um plano quando o servidor está indisponível', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Network error')));
    render(<App />); fireEvent.click(screen.getByRole('button', { name: /Traçar minha rota/ }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Confira se o servidor está iniciado');
    expect(screen.queryByText('Você tem um caminho.')).not.toBeInTheDocument();
  });
  it('exige disponibilidade e autorização separadas para simular reset', () => {
    render(<App />);
    const permission = screen.getByRole('checkbox', { name: /Permitir simulação/ });
    expect(permission).toBeDisabled();
    fireEvent.click(screen.getByRole('checkbox', { name: /Ainda tenho o reset/ }));
    expect(permission).not.toBeDisabled(); expect(permission).not.toBeChecked();
    fireEvent.click(permission); expect(permission).toBeChecked();
    fireEvent.click(screen.getByRole('checkbox', { name: /Ainda tenho o reset/ }));
    expect(permission).not.toBeChecked(); expect(permission).toBeDisabled();
  });
  it('mostra a mensagem de entrada inválida recebida do motor', async () => {
    vi.stubGlobal('fetch', vi.fn().mockResolvedValue({ ok: false, json: async () => ({ code: 'INVALID_REQUEST', message: 'Orçamento de atributos excedido.' }) }));
    render(<App />); fireEvent.click(screen.getByRole('button', { name: /Traçar minha rota/ }));
    expect(await screen.findByRole('alert')).toHaveTextContent('Orçamento de atributos excedido.');
  });
});
