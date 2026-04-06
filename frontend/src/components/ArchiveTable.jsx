import React from 'react';

const ArchiveTable = ({ products, onRestore, styles }) => {
    return (
        <table style={styles.table}>
            <thead>
            <tr>
                <th style={styles.th}>Фото</th>
                <th style={styles.th}>Название</th>
                <th style={styles.th}>Статус</th>
                <th style={styles.th}>Действия</th>
            </tr>
            </thead>
            <tbody>
            {products.map(p => (
                <tr key={p.id} style={styles.tr}>
                    <td style={styles.td}>
                        <img src={p.imageUrl} alt="" style={{...styles.img, filter: 'grayscale(1)'}}/>
                    </td>
                    <td style={{...styles.td, color: '#999'}}>{p.name}</td>
                    <td style={styles.td}>
                        <span style={{color: '#ff4d4f'}}>Архив</span>
                    </td>
                    <td style={styles.td}>
                        <button style={styles.btn} onClick={() => onRestore(p.id)}>
                            ВОССТАНОВИТЬ
                        </button>
                    </td>
                </tr>
            ))}
            </tbody>
        </table>
    );
};

export default ArchiveTable;