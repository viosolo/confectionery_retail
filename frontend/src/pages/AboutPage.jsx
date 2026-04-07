import React from 'react';

const AboutPage = () => {
    return (
        <div style={container}>
            <header style={headerSection}>
                <span style={label}>Handmade Confectionery</span>
                <h1 style={mainTitle}>VIOSOLOCAKE</h1>
                <div style={divider}></div>
            </header>

            <section style={introBlock}>
                <div style={contentWrapper}>
                    <h2 style={sectionTitle}>Меня зовут Виолетта</h2>
                    <p style={text}>
                        Viosolocake — это моя домашняя мастерская, где каждый завиток десерта создается вручную.
                        Моя философия проста: десерт должен быть не только красивым, но и полезным.
                    </p>
                    <p style={text}>
                        Я не использую соки — только густое <b>натуральное пюре</b>, которое готовлю сама.
                        В моем зефире значительно меньше сахара, а за пользу отвечают агар-агар и пектин.
                    </p>
                </div>
            </section>

            <section style={productsGrid}>
                <div style={glassCard}>
                    <div style={cardBadge}>Classic</div>
                    <h3 style={cardTitle}>Бисквитные торты</h3>
                    <p style={cardDesc}>Нежные пропитанные коржи, легкие кремы и натуральные начинки.</p>
                </div>
                <div style={{...glassCard, backgroundColor: '#fdf7f8'}}>
                    <div style={cardBadge}>Modern</div>
                    <h3 style={cardTitle}>Муссовые торты</h3>
                    <p style={cardDesc}>Изысканное сочетание текстур: зеркальная глазурь, воздушный мусс и фруктовый центр.</p>
                </div>
            </section>
        </div>
    );
};

const container = { padding: '60px 20px', maxWidth: '1000px', margin: '0 auto', fontFamily: 'var(--body-font)', color: '#333' };
const headerSection = { textAlign: 'center', marginBottom: '80px' };
const label = { fontSize: '10px', letterSpacing: '4px', textTransform: 'uppercase', color: '#bca0a5', display: 'block', marginBottom: '10px' };
const mainTitle = { fontSize: '3.5rem', fontWeight: '300', letterSpacing: '12px', margin: '0' };
const divider = { width: '40px', height: '2px', background: '#333', margin: '20px auto' };
const introBlock = { marginBottom: '60px', padding: '40px', borderRadius: '40px', background: '#fff', border: '1px solid #f0f0f0', boxShadow: '0 10px 30px rgba(0,0,0,0.02)' };
const contentWrapper = { maxWidth: '700px', margin: '0 auto', textAlign: 'center' };
const sectionTitle = { fontSize: '1.8rem', fontWeight: '400', marginBottom: '25px', letterSpacing: '1px' };
const text = { lineHeight: '1.9', color: '#666', fontSize: '1.1rem', marginBottom: '20px' };
const productsGrid = { display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))', gap: '30px' };
const glassCard = { padding: '40px', borderRadius: '35px', background: '#fafafa', border: '1px solid #eee', position: 'relative', textAlign: 'center', transition: 'transform 0.3s' };
const cardBadge = { position: 'absolute', top: '20px', left: '50%', transform: 'translateX(-50%)', fontSize: '9px', textTransform: 'uppercase', letterSpacing: '2px', color: '#bca0a5' };
const cardTitle = { fontSize: '1.4rem', marginTop: '10px', marginBottom: '15px', fontWeight: '500' };
const cardDesc = { fontSize: '0.95rem', color: '#888', lineHeight: '1.6' };

export default AboutPage;