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
                        Viosolocake — это моя авторская кондитерская мастерская. Здесь объединяются эстетика формы
                        и чистота вкуса. Моя работа основана на принципах честного состава и ручного труда
                        на каждом этапе создания десерта.
                    </p>
                    <p style={text}>
                        Я верю, что кондитерское искусство — это баланс между качественными ингредиентами и
                        вниманием к деталям. Я создаю десерты, которые приносят радость, сохраняя при этом
                        натуральность и пользу.
                    </p>
                </div>
            </section>

            <div style={catalogHeader}>
                <h2 style={catalogTitle}>Моя коллекция десертов</h2>
                <p style={catalogSubtitle}>Тщательно проработанные рецептуры для ваших особенных моментов</p>
            </div>

            <section style={productsGrid}>
                <div style={glassCard}>
                    <div style={cardBadge}>Signature</div>
                    <h3 style={cardTitle}>Натуральный зефир</h3>
                    <p style={cardDesc}>
                        Уникальная технология на основе густого домашнего пюре. Минимум сахара,
                        максимум ягодного вкуса и польза натурального агар-агара.
                    </p>
                </div>
                <div style={glassCard}>
                    <div style={cardBadge}>Classic</div>
                    <h3 style={cardTitle}>Бисквитные торты</h3>
                    <p style={cardDesc}>
                        Классика в современном прочтении. Нежные коржи, легкие муссовые
                        прослойки и только натуральные сливки и ягоды.
                    </p>
                </div>
                <div style={{...glassCard, backgroundColor: '#fdf7f8'}}>
                    <div style={cardBadge}>Modern</div>
                    <h3 style={cardTitle}>Муссовые торты</h3>
                    <p style={cardDesc}>
                        Сложные сочетания текстур, тонкие акценты и изысканный декор
                        в стиле минимализма для истинных ценителей.
                    </p>
                </div>
            </section>
        </div>
    );
};

const container = {
    padding: '60px 20px',
    maxWidth: '1200px',
    margin: '0 auto',
    fontFamily: "'Helvetica Neue', Arial, sans-serif",
    color: '#1d1d1f'
};

const headerSection = {
    textAlign: 'center',
    marginBottom: '80px'
};

const label = {
    fontSize: '14px',
    letterSpacing: '5px',
    textTransform: 'uppercase',
    color: '#bca0a5',
    display: 'block',
    marginBottom: '15px'
};

const mainTitle = {
    fontSize: '3.5rem',
    fontWeight: '300',
    letterSpacing: '15px',
    margin: '0',
    color: '#1d1d1f'
};

const divider = {
    width: '50px',
    height: '1px',
    background: '#d2d2d7',
    margin: '30px auto'
};

const introBlock = {
    marginBottom: '100px',
    padding: '70px 50px',
    borderRadius: '40px',
    background: '#fff',
    border: '1px solid #f5f5f7',
    boxShadow: '0 4px 25px rgba(0,0,0,0.01)'
};

const contentWrapper = {
    maxWidth: '850px',
    margin: '0 auto',
    textAlign: 'center'
};

const sectionTitle = {
    fontSize: '2.4rem',
    fontWeight: '400',
    marginBottom: '35px',
    color: '#1d1d1f'
};

const text = {
    lineHeight: '1.8',
    color: '#424245',
    fontSize: '1.25rem',
    marginBottom: '30px'
};

const catalogHeader = {
    textAlign: 'center',
    marginBottom: '60px'
};

const catalogTitle = {
    fontSize: '2.6rem',
    fontWeight: '300',
    marginBottom: '15px',
    color: '#1d1d1f'
};

const catalogSubtitle = {
    fontSize: '1.25rem',
    color: '#86868b'
};

const productsGrid = {
    display: 'grid',
    gridTemplateColumns: 'repeat(auto-fit, minmax(340px, 1fr))',
    gap: '40px'
};

const glassCard = {
    padding: '60px 45px',
    borderRadius: '35px',
    background: '#fafafa',
    border: '1px solid #f0f0f2',
    position: 'relative',
    textAlign: 'center'
};

const cardBadge = {
    position: 'absolute',
    top: '30px',
    left: '50%',
    transform: 'translateX(-50%)',
    fontSize: '13px',
    textTransform: 'uppercase',
    letterSpacing: '3px',
    color: '#bca0a5',
    fontWeight: '600'
};

const cardTitle = {
    fontSize: '1.8rem',
    marginTop: '25px',
    marginBottom: '15px',
    fontWeight: '500',
    color: '#1d1d1f'
};

const cardDesc = {
    fontSize: '1.15rem',
    color: '#6e6e73',
    lineHeight: '1.7'
};

export default AboutPage;