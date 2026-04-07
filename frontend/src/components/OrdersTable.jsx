import React from 'react';

const ORDER_STATUSES = [
    { id: 'PENDING', label: 'Ожидает' },
    { id: 'CONFIRMED', label: 'Подтвержден' },
    { id: 'PROCESSING', label: 'Готовится' },
    { id: 'SHIPPED', label: 'В пути' },
    { id: 'DELIVERED', label: 'Доставлен' },
    { id: 'CANCELLED', label: 'Отменен' }
];

const OrdersTable = ({ orders, onUpdateStatus, onDeleteOrder, onViewDetails, styles }) => {
    return (
        <table style={styles.table}>
            <thead>
            <tr>
                <th style={styles.th}>Заказ</th>
                <th style={styles.th}>Дата</th>
                <th style={styles.th}>Клиент</th>
                <th style={styles.th}>Сумма</th>
                <th style={styles.th}>Статус</th>
                <th style={styles.th}></th>
            </tr>
            </thead>
            <tbody>
            {orders.map(o => (
                <tr key={o.id} style={styles.tr}>
                    <td style={{...styles.td, fontWeight: '700', color: '#000'}}>{o.orderNumber}</td>
                    <td style={styles.td}>{new Date(o.createdAt).toLocaleDateString()}</td>
                    <td style={styles.td}>
                        <div style={{fontWeight: '500'}}>{o.userName || 'Гость'}</div>
                        <div style={{fontSize: '0.75rem', color: '#aaa'}}>{o.guestPhone || o.userEmail}</div>
                    </td>
                    <td style={{...styles.td, fontWeight: '600'}}>{o.totalAmount} BYN</td>
                    <td style={styles.td}>
                        <select
                            value={o.status}
                            onChange={(e) => onUpdateStatus(o.id, e.target.value)}
                            style={styles.select}
                        >
                            {ORDER_STATUSES.map(s => (
                                <option key={s.id} value={s.id}>{s.label}</option>
                            ))}
                        </select>
                    </td>
                    <td style={styles.td}>
                        <div style={{display: 'flex', alignItems: 'center', gap: '12px'}}>
                            <button onClick={() => onViewDetails(o)} style={detailBtnStyle}>ДЕТАЛИ</button>
                            <button
                                onClick={() => onDeleteOrder(o.id, o.orderNumber)}
                                style={deleteIconStyle}
                            >
                                🗑️
                            </button>
                        </div>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

const detailBtnStyle = {
    padding: '6px 12px',
    background: '#fff',
    border: '1px solid #000',
    borderRadius: '6px',
    fontSize: '0.65rem',
    cursor: 'pointer',
    letterSpacing: '1px',
    transition: '0.2s'
};

const deleteIconStyle = {
    background: 'none',
    border: 'none',
    cursor: 'pointer',
    fontSize: '1.1rem',
    color: '#ff4d4f',
    opacity: 0.7,
    transition: '0.2s'
};

export default OrdersTable;