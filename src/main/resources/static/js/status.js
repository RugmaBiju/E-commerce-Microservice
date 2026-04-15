/**
 * Status.js
 * Handles animations and interactions on success/failure pages
 */

document.addEventListener('DOMContentLoaded', function () {
    console.log('Status page initialized');

    // Get the status container
    const statusCard = document.querySelector('.status-card');

    if (statusCard) {
        // Add animation class
        statusCard.classList.add('animated');

        // Animate SVG elements if present
        const svgElements = statusCard.querySelectorAll('svg');
        svgElements.forEach(svg => {
            const circles = svg.querySelectorAll('circle');
            const paths = svg.querySelectorAll('path');
            const lines = svg.querySelectorAll('line');

            // Animate circles
            circles.forEach(circle => {
                const circumference = 2 * Math.PI * circle.getAttribute('r');
                circle.style.strokeDasharray = circumference;
                circle.style.strokeDashoffset = circumference;
                
                setTimeout(() => {
                    circle.style.transition = 'stroke-dashoffset 0.5s ease-out';
                    circle.style.strokeDashoffset = '0';
                }, 100);
            });

            // Animate paths and lines
            const elements = [...paths, ...lines];
            elements.forEach((element, index) => {
                const length = element.getTotalLength();
                element.style.strokeDasharray = length;
                element.style.strokeDashoffset = length;

                setTimeout(() => {
                    element.style.transition = `stroke-dashoffset 0.5s ease-out ${0.2 + index * 0.1}s`;
                    element.style.strokeDashoffset = '0';
                }, 100);
            });
        });
    }

    // Add button interactions
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('click', function(e) {
            // Add ripple effect
            const rect = this.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;

            const ripple = document.createElement('span');
            ripple.style.position = 'absolute';
            ripple.style.left = x + 'px';
            ripple.style.top = y + 'px';
            ripple.style.width = '0';
            ripple.style.height = '0';
            ripple.style.borderRadius = '50%';
            ripple.style.background = 'rgba(255, 255, 255, 0.5)';
            ripple.style.pointerEvents = 'none';

            this.style.position = 'relative';
            this.style.overflow = 'hidden';
            this.appendChild(ripple);

            ripple.style.transition = 'width 0.6s, height 0.6s, opacity 0.6s';
            ripple.style.width = '300px';
            ripple.style.height = '300px';
            ripple.style.opacity = '0';

            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });

    // Add smooth scroll behavior
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                target.scrollIntoView({
                    behavior: 'smooth',
                    block: 'start'
                });
            }
        });
    });

    // Show details with animation
    const detailsGrid = document.querySelector('.details-grid');
    if (detailsGrid) {
        const detailRows = detailsGrid.querySelectorAll('.detail-row');
        detailRows.forEach((row, index) => {
            row.style.opacity = '0';
            row.style.transform = 'translateY(10px)';
            
            setTimeout(() => {
                row.style.transition = 'opacity 0.5s ease-out, transform 0.5s ease-out';
                row.style.opacity = '1';
                row.style.transform = 'translateY(0)';
            }, 300 + index * 100);
        });
    }

    // Print invoice functionality
    window.printInvoice = function() {
        window.print();
    };

    // Copy text to clipboard
    window.copyToClipboard = function(text) {
        navigator.clipboard.writeText(text).then(() => {
            alert('Copied to clipboard!');
        }).catch(err => {
            console.error('Failed to copy:', err);
        });
    };

    console.log('Status page setup complete');
});
