export const attributeKeys = ['body', 'reflexes', 'technicalAbility', 'intelligence', 'cool'] as const;
export type Attribute = typeof attributeKeys[number];
export type Attributes = Record<Attribute, number>;
export const labels: Record<Attribute, string> = { body: 'Corpo', reflexes: 'Reflexos', technicalAbility: 'Habilidade técnica', intelligence: 'Inteligência', cool: 'Moral' };
export interface PlanRequest {
  rulesetId: string; level: number; targetLevel: number; phantomLiberty: boolean;
  resetAvailable: boolean; allowReset: boolean; attributes: Attributes; targetAttributes: Attributes;
}
export interface PlanResponse {
  rulesetId: string; status: 'REACHABLE' | 'REQUIRES_RESET' | 'UNREACHABLE'; usesReset: boolean;
  unspentNow: number; pointsNeeded: number; pointsAvailable: number; shortfall: number; earliestLevel: number | null;
  finalAttributes: Attributes; steps: { level: number; attribute: Attribute; from: number; to: number }[]; explanations: string[];
}
export async function calculatePlan(request: PlanRequest, signal: AbortSignal): Promise<PlanResponse> {
  let response: Response;
  try { response = await fetch('/api/v1/plans', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(request), signal }); }
  catch (error) {
    if (signal.aborted) throw error;
    throw new Error('Não foi possível conectar ao ChromeLab. Confira se o servidor está iniciado e tente novamente.');
  }
  if (!response.ok) {
    const error = await response.json().catch(() => null) as { message?: string } | null;
    throw new Error(error?.message || 'O servidor não conseguiu calcular a rota. Tente novamente.');
  }
  return response.json() as Promise<PlanResponse>;
}
