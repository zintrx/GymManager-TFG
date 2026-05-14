document.addEventListener('DOMContentLoaded', () => {
    const userData = JSON.parse(localStorage.getItem('gym_user'));
    if (!userData) { window.location.href = 'index.html'; return; }

    const isAdmin = userData.role === 'ADMIN';

    // --- Constants & State ---
    const DAYS_ES = ['DO', 'LU', 'MA', 'MI', 'JU', 'VI', 'SA'];
    const MONTHS_ES = ['Enero','Febrero','Marzo','Abril','Mayo','Junio','Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'];

    const BASE_CLASSES = [
        { id: 'cls1', title: 'Spinning Pro',     emoji: '🚴', color: '#BBFF00', bg: 'rgba(187,255,0,0.12)',  time: '07:00', duration: '45 min', room: 'Sala Bici',      instructor: 'Marta V.',  capacity: 20 },
        { id: 'cls2', title: 'BodyPump Extreme', emoji: '🏋️', color: '#00E0FF', bg: 'rgba(0,224,255,0.12)', time: '09:30', duration: '60 min', room: 'Sala Musculación', instructor: 'Carlos D.', capacity: 15 },
        { id: 'cls3', title: 'CrossFit WOD',     emoji: '🔥', color: '#ef4444', bg: 'rgba(239,68,68,0.12)',  time: '11:00', duration: '60 min', room: 'Box Principal',    instructor: 'Alex G.',   capacity: 12 },
        { id: 'cls4', title: 'Yoga Flow',         emoji: '🧘', color: '#a78bfa', bg: 'rgba(167,139,250,0.12)',time: '13:15', duration: '50 min', room: 'Sala Zen',         instructor: 'Sofía R.',  capacity: 10 },
        { id: 'cls5', title: 'HIIT Explosivo',   emoji: '⚡', color: '#f59e0b', bg: 'rgba(245,158,11,0.12)', time: '17:30', duration: '40 min', room: 'Funcional',        instructor: 'Marc T.',   capacity: 25 },
        { id: 'cls6', title: 'Zumba Party',      emoji: '💃', color: '#ec4899', bg: 'rgba(236,72,153,0.12)', time: '19:00', duration: '55 min', room: 'Sala Principal',   instructor: 'Elena P.',  capacity: 30 },
        { id: 'cls7', title: 'Pilates Reformer', emoji: '🤸', color: '#34d399', bg: 'rgba(52,211,153,0.12)', time: '20:15', duration: '45 min', room: 'Sala Pilates',     instructor: 'Diana M.',  capacity: 8  },
    ];

    // Simulate different booking counts per day using modulo
    const getBookedCount = (classId, dayOffset) => {
        const seed = parseInt(classId.replace('cls','')) * (dayOffset + 1);
        const base = BASE_CLASSES.find(c => c.id === classId);
        return Math.min(base.capacity, Math.floor((seed * 3) % base.capacity));
    };

    // User reservations per day: key = `gym_res_{username}_{dateStr}`
    const getResKey = (dateStr) => `gym_res_${userData.username}_${dateStr}`;

    // Selected day state
    let selectedDate = new Date();
    let selectedDayOffset = 0; // 0 = today

    // --- DOM Refs ---
    const calDateLabel    = document.getElementById('calDateLabel');
    const weekStrip       = document.getElementById('weekStrip');
    const classesContainer = document.getElementById('classesContainer');
    const daySection      = document.getElementById('daySection');
    const reservCount     = document.getElementById('myReservationsCount');
    const fabAdmin        = document.getElementById('fabAddClass');
    const actModal        = document.getElementById('activityModal');
    const closeActBtn     = document.getElementById('closeActivityModal');
    const toastEl         = document.getElementById('toastMsg');

    if (isAdmin) fabAdmin.classList.add('visible');

    // --- Toast ---
    function showToast(msg, duration = 2500) {
        toastEl.textContent = msg;
        toastEl.classList.add('show');
        setTimeout(() => toastEl.classList.remove('show'), duration);
    }

    // --- Week Strip ---
    function buildWeekStrip() {
        weekStrip.innerHTML = '';
        for (let i = -2; i <= 4; i++) {
            const d = new Date();
            d.setDate(d.getDate() + i);
            const chip = document.createElement('div');
            chip.className = 'day-chip' + (i === 0 ? ' active' : '');
            chip.innerHTML = `<span>${DAYS_ES[d.getDay()]}</span><strong>${d.getDate()}</strong>`;
            chip.dataset.offset = i;
            chip.addEventListener('click', () => selectDay(d, i, chip));
            weekStrip.appendChild(chip);
        }
    }

    function selectDay(date, offset, chip) {
        selectedDate  = date;
        selectedDayOffset = offset;
        document.querySelectorAll('.day-chip').forEach(c => c.classList.remove('active'));
        chip.classList.add('active');
        renderClasses();
    }

    // --- Classes Render ---
    function renderClasses() {
        const dateStr = selectedDate.toISOString().split('T')[0];
        const weekday = DAYS_ES[selectedDate.getDay()];
        const dayNum  = selectedDate.getDate();
        const month   = MONTHS_ES[selectedDate.getMonth()];

        calDateLabel.textContent = `${weekday} ${dayNum} de ${month}`;
        daySection.textContent   = selectedDayOffset === 0 ? 'Clases de hoy' : `Clases del ${dayNum} de ${month}`;

        const userRes = JSON.parse(localStorage.getItem(getResKey(dateStr))) || [];

        // Get any extra admin-created classes for this day
        const extraKey = `gym_extra_classes_${dateStr}`;
        const extraClasses = JSON.parse(localStorage.getItem(extraKey)) || [];
        const allClasses = [...BASE_CLASSES, ...extraClasses];

        classesContainer.innerHTML = '';
        allClasses.forEach(cls => {
            const booked   = getBookedCount(cls.id, selectedDayOffset + 2);
            const isBooked = userRes.includes(cls.id);
            const isFull   = booked >= cls.capacity && !isBooked;

            const card = document.createElement('div');
            card.className = 'class-card' + (isBooked ? ' reserved' : '');

            const pct = Math.round((booked / cls.capacity) * 100);
            const barColor = pct >= 90 ? '#ef4444' : pct >= 60 ? '#f59e0b' : 'var(--primary)';

            card.innerHTML = `
                <div class="class-color-badge" style="background:${cls.bg}; font-size: 1.6rem;">${cls.emoji}</div>
                <div class="class-info">
                    <h4>${cls.title} ${isBooked ? '<span class="badge-reserved">✓ Reservada</span>' : ''}</h4>
                    <div class="class-meta">
                        <span><i class="far fa-clock"></i> ${cls.time} (${cls.duration})</span>
                        <span><i class="fas fa-map-marker-alt"></i> ${cls.room}</span>
                        <span><i class="fas fa-user-tie"></i> ${cls.instructor}</span>
                    </div>
                    <div class="aforo-bar-wrap">
                        <div class="aforo-bar-bg">
                            <div class="aforo-bar-fill" style="width:${pct}%; background:${barColor};"></div>
                        </div>
                        <span style="font-size:0.7rem; color:var(--text-muted); flex-shrink:0;">${booked}/${cls.capacity}</span>
                    </div>
                </div>
            `;

            // Action button
            if (isBooked) {
                const btn = document.createElement('button');
                btn.className = 'btn-cancel-res';
                btn.innerHTML = '<i class="fas fa-times"></i>';
                btn.title = 'Cancelar reserva';
                btn.onclick = (e) => { e.stopPropagation(); toggleReservation(cls.id, dateStr, userRes); };
                card.appendChild(btn);
            } else if (isFull) {
                const badge = document.createElement('span');
                badge.className = 'badge-full';
                badge.textContent = 'LLENO';
                card.appendChild(badge);
            } else {
                const btn = document.createElement('button');
                btn.className = 'btn-reserve';
                btn.textContent = 'Reservar';
                btn.onclick = (e) => { e.stopPropagation(); toggleReservation(cls.id, dateStr, userRes); };
                card.appendChild(btn);
            }

            classesContainer.appendChild(card);
        });

        // Update reservation counter
        reservCount.textContent = `${userRes.length} ${userRes.length === 1 ? 'Reserva' : 'Reservas'}`;
    }

    function toggleReservation(classId, dateStr, currentRes) {
        let userRes = [...currentRes];
        if (userRes.includes(classId)) {
            userRes = userRes.filter(id => id !== classId);
            showToast('❌ Reserva cancelada');
        } else {
            userRes.push(classId);
            showToast('✅ ¡Clase reservada con éxito!');
        }
        localStorage.setItem(getResKey(dateStr), JSON.stringify(userRes));
        renderClasses();
    }

    // --- Admin: add class form ---
    if (fabAdmin) {
        fabAdmin.onclick = () => { actModal.style.display = 'flex'; };
    }
    if (closeActBtn) {
        closeActBtn.onclick = () => { actModal.style.display = 'none'; };
    }

    const addForm = document.getElementById('addActivityForm');
    if (addForm) {
        addForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const dateStr = selectedDate.toISOString().split('T')[0];
            const extraKey = `gym_extra_classes_${dateStr}`;
            const extras = JSON.parse(localStorage.getItem(extraKey)) || [];
            const newCls = {
                id:         'extra_' + Date.now(),
                title:      document.getElementById('actTitle').value,
                emoji:      '⭐',
                color:      '#BBFF00',
                bg:         'rgba(187,255,0,0.12)',
                time:       document.getElementById('actTime').value,
                duration:   document.getElementById('actDuration').value || '60 min',
                room:       document.getElementById('actSala').value,
                instructor: document.getElementById('actInstructor').value || 'GymManager',
                capacity:   parseInt(document.getElementById('actCapacity').value) || 20,
            };
            extras.push(newCls);
            localStorage.setItem(extraKey, JSON.stringify(extras));
            actModal.style.display = 'none';
            addForm.reset();
            renderClasses();
            showToast('🎉 Clase añadida correctamente');
        });
    }

    // Close modal on outside click
    window.addEventListener('click', (e) => {
        if (e.target === actModal) actModal.style.display = 'none';
    });

    // --- Init ---
    buildWeekStrip();
    renderClasses();
});
