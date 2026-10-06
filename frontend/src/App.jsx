import React, { useCallback, useEffect, useMemo, useRef, useState } from 'react';

async function api(path, options) {
  const res = await fetch(`/api${path}`, options);
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) throw new Error(data?.error || `Request failed (${res.status})`);
  return data;
}

export default function App() {
  const [meta, setMeta] = useState(null);
  const [active, setActive] = useState('student');
  const [loaded, setLoaded] = useState({ key: null, data: [] });
  const latest = useRef(0);
  const [loading, setLoading] = useState(true);
  const [fatal, setFatal] = useState(null);
  const [filter, setFilter] = useState('');
  const [drawer, setDrawer] = useState(false);
  const [confirmKey, setConfirmKey] = useState(null);
  const [notice, setNotice] = useState(null);

  const flash = (text, kind = 'ok') => {
    setNotice({ text, kind });
    setTimeout(() => setNotice(null), 3500);
  };

  const loadMeta = useCallback(async () => {
    try { setMeta(await api('/meta')); setFatal(null); }
    catch (e) { setFatal(e.message); }
  }, []);

  const loadRows = useCallback(async (key) => {
    const id = ++latest.current;
    setLoading(true);
    try {
      const data = await api(`/tables/${key}`);
      if (id === latest.current) setLoaded({ key, data });
    } catch (e) { if (id === latest.current) flash(e.message, 'err'); }
    finally { if (id === latest.current) setLoading(false); }
  }, []);

  useEffect(() => { loadMeta(); }, [loadMeta]);
  const ready = meta !== null;
  useEffect(() => {
    if (ready) { loadRows(active); setFilter(''); setConfirmKey(null); }
  }, [active, ready, loadRows]);

  const table = meta?.[active];
  const rows = loaded.key === active ? loaded.data : [];
  const rowKey = (r) => table.pk.map((k) => r[k]).join('|');

  const visible = useMemo(() => {
    const q = filter.trim().toLowerCase();
    if (!q || !table) return rows;
    return rows.filter((r) => table.columns.some((c) => String(r[c.key] ?? '').toLowerCase().includes(q)));
  }, [rows, filter, table]);

  async function remove(row) {
    const qs = table.pk.map((k) => `${k}=${encodeURIComponent(row[k])}`).join('&');
    try {
      await api(`/tables/${active}?${qs}`, { method: 'DELETE' });
      flash(`Deleted ${table.singular}.`);
      setConfirmKey(null);
      await Promise.all([loadRows(active), loadMeta()]);
    } catch (e) { flash(e.message, 'err'); setConfirmKey(null); }
  }

  async function add(values) {
    await api(`/tables/${active}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(values),
    });
    flash(`Added ${table.singular}.`);
    setDrawer(false);
    await Promise.all([loadRows(active), loadMeta()]);
  }

  if (fatal) {
    return (
      <div className="fatal">
        <h1>Can't reach the database</h1>
        <p>{fatal}</p>
        <p className="muted">Start the Java API (<code>mvn compile exec:java</code> in /backend) and make sure MySQL is running with the credentials in <code>backend/db.properties</code>.</p>
        <button className="btn" onClick={loadMeta}>Retry</button>
      </div>
    );
  }
  if (!meta) return <div className="fatal"><p className="muted">Connecting…</p></div>;

  return (
    <div className="shell">
      <aside className="side">
        <div className="brand">
          <span className="brand-mark">▌</span>
          <div>
            <div className="brand-name">campus_register</div>
            <div className="brand-sub">student_college_management</div>
          </div>
        </div>
        <nav>
          <div className="nav-label">TABLES</div>
          {Object.values(meta).map((t) => (
            <button key={t.key} className={`nav-item ${t.key === active ? 'on' : ''}`} onClick={() => setActive(t.key)}>
              <span>{t.label}</span>
              <span className="count">{t.count}</span>
            </button>
          ))}
        </nav>
        <div className="side-foot">mysql · java · react</div>
      </aside>

      <main className="main">
        <header className="bar">
          <div>
            <h1>{table.label}</h1>
            <div className="muted small">{table.count} record{table.count === 1 ? '' : 's'} in <code>{table.key}</code></div>
          </div>
          <div className="bar-actions">
            <input className="search" placeholder="filter rows…" value={filter} onChange={(e) => setFilter(e.target.value)} />
            <button className="btn primary" onClick={() => setDrawer(true)}>+ New {table.singular}</button>
          </div>
        </header>

        <div className="sheet">
          <table className="grid">
            <thead>
              <tr>
                {table.columns.map((c) => <th key={c.key}>{c.label}</th>)}
                <th className="act-col" />
              </tr>
            </thead>
            <tbody>
              {visible.map((r) => {
                const k = rowKey(r);
                return (
                  <tr key={k}>
                    {table.columns.map((c) => (
                      <td key={c.key} className={c.key.endsWith('_id') ? 'id' : ''}>
                        {r[c.key] ?? <span className="null">null</span>}
                      </td>
                    ))}
                    <td className="act-col">
                      {confirmKey === k ? (
                        <span className="confirm">
                          delete?
                          <button className="link danger" onClick={() => remove(r)}>yes</button>
                          <button className="link" onClick={() => setConfirmKey(null)}>no</button>
                        </span>
                      ) : (
                        <button className="link danger" onClick={() => setConfirmKey(k)}>delete</button>
                      )}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
          {!loading && visible.length === 0 && (
            <div className="empty">{rows.length ? 'No rows match the filter.' : 'No records yet.'}</div>
          )}
          {loading && <div className="empty">Loading…</div>}
        </div>
      </main>

      {drawer && <Drawer table={table} onClose={() => setDrawer(false)} onSubmit={add} />}
      {notice && <div className={`toast ${notice.kind}`}>{notice.text}</div>}
    </div>
  );
}

function Drawer({ table, onClose, onSubmit }) {
  const [values, setValues] = useState({});
  const [options, setOptions] = useState({});
  const [error, setError] = useState(null);
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    table.fields.filter((f) => f.type === 'ref').forEach(async (f) => {
      try {
        const opts = await api(`/options/${f.ref}`);
        setOptions((o) => ({ ...o, [f.name]: opts }));
      } catch (e) { setError(e.message); }
    });
  }, [table]);

  const set = (k, v) => setValues((s) => ({ ...s, [k]: v }));

  async function submit(e) {
    e.preventDefault();
    setBusy(true); setError(null);
    try { await onSubmit(values); }
    catch (err) { setError(err.message); setBusy(false); }
  }

  return (
    <div className="scrim" onMouseDown={(e) => e.target === e.currentTarget && onClose()}>
      <form className="drawer" onSubmit={submit}>
        <div className="drawer-head">
          <h2>New {table.singular}</h2>
          <button type="button" className="link" onClick={onClose}>esc ✕</button>
        </div>
        <div className="drawer-body">
          {table.fields.map((f) => (
            <label key={f.name} className="field">
              <span>{f.label}{f.required && <b className="req"> *</b>}</span>
              {f.type === 'ref' ? (
                <select value={values[f.name] ?? ''} onChange={(e) => set(f.name, e.target.value)}>
                  <option value="">{f.required ? '— select —' : '— none —'}</option>
                  {(options[f.name] || []).map((o) => <option key={o.id} value={o.id}>{o.label}</option>)}
                </select>
              ) : f.type === 'choice' ? (
                <select value={values[f.name] ?? ''} onChange={(e) => set(f.name, e.target.value)}>
                  <option value="">— none —</option>
                  {f.choices.map((c) => <option key={c} value={c}>{c}</option>)}
                </select>
              ) : (
                <input
                  type={f.type === 'number' ? 'number' : f.type === 'email' ? 'email' : 'text'}
                  min={f.type === 'number' ? 1 : undefined}
                  max={f.type === 'number' ? 6 : undefined}
                  value={values[f.name] ?? ''}
                  onChange={(e) => set(f.name, e.target.value)}
                  autoFocus={f === table.fields[0]}
                />
              )}
            </label>
          ))}
          {error && <div className="form-error">{error}</div>}
        </div>
        <div className="drawer-foot">
          <button type="button" className="btn" onClick={onClose}>Cancel</button>
          <button className="btn primary" disabled={busy}>{busy ? 'Saving…' : 'Insert record'}</button>
        </div>
      </form>
    </div>
  );
}
