import React from 'react';

const DeliveryPage = () => {
    return (
        <div style={container}>
            <h1 style={pageTitle}>ДОСТАВКА И ОПЛАТА</h1>

            <div style={grid}>
                <div style={infoBox}>
                    <div style={header}>
                        <span style={number}>01</span>
                        <h2 style={boxTitle}>Как получить</h2>
                    </div>
                    <div style={item}>
                        <span style={bold}>Самовывоз:</span>
                        <p style={detail}>Минск, ЖК Минск-Мир — Бесплатно</p>
                    </div>
                    <div style={item}>
                        <span style={bold}>Курьер:</span>
                        <p style={detail}>По городу — 10 BYN</p>
                    </div>
                    <div style={highlightItem}>
                        <p style={{margin: 0}}><b>Бесплатно</b> при заказе от 50 BYN</p>
                    </div>
                </div>

                <div style={infoBox}>
                    <div style={header}>
                        <span style={number}>02</span>
                        <h2 style={boxTitle}>Как оплатить</h2>
                    </div>
                    <p style={text}>Оплата производится <b>при получении</b> заказа.</p>
                    <div style={paymentMethods}>
                        <div style={method}>Наличные</div>
                        <div style={method}>Карта (самовывоз)</div>
                    </div>
                </div>
            </div>

            <footer style={warningFooter}>
                <p>Пожалуйста, делайте заказ за <b>2-3 дня</b>. Каждое изделие готовится индивидуально.</p>
            </footer>
        </div>
    );
};

const container = { padding: '80px 20px', maxWidth: '1100px', margin: '0 auto', fontFamily: 'var(--body-font)' };
const pageTitle = { textAlign: 'center', fontSize: '2.5rem', fontWeight: '300', letterSpacing: '10px', marginBottom: '60px' };
const grid = { display: 'flex', gap: '40px', flexWrap: 'wrap' };
const infoBox = { flex: '1', minWidth: '350px', background: '#fff', padding: '50px', borderRadius: '40px', border: '1px solid #f0f0f0', boxShadow: '0 15px 40px rgba(0,0,0,0.03)' };
const header = { display: 'flex', alignItems: 'center', gap: '15px', marginBottom: '30px' };
const number = { fontSize: '0.8rem', color: '#ffb7c5', fontWeight: '700', borderBottom: '2px solid #ffb7c5' };
const boxTitle = { fontSize: '1.5rem', fontWeight: '400', margin: 0 };
const item = { marginBottom: '20px' };
const bold = { fontSize: '0.9rem', textTransform: 'uppercase', color: '#bca0a5', letterSpacing: '1px' };
const detail = { margin: '5px 0 0', fontSize: '1.1rem', color: '#333' };
const highlightItem = { marginTop: '30px', padding: '15px', background: '#fdf7f8', borderRadius: '15px', textAlign: 'center', color: '#d48a97' };
const text = { fontSize: '1.1rem', lineHeight: '1.7', color: '#666' };
const paymentMethods = { display: 'flex', gap: '10px', marginTop: '20px' };
const method = { padding: '10px 20px', border: '1px solid #eee', borderRadius: '12px', fontSize: '0.85rem', color: '#888' };
const warningFooter = { marginTop: '60px', textAlign: 'center', color: '#bbb', fontSize: '0.9rem', letterSpacing: '0.5px' };

export default DeliveryPage;