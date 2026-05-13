---
name: Convive
colors:
  surface: '#f8f9ff'
  surface-dim: '#cbdbf5'
  surface-bright: '#f8f9ff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4ff'
  surface-container: '#e5eeff'
  surface-container-high: '#dce9ff'
  surface-container-highest: '#d3e4fe'
  on-surface: '#0b1c30'
  on-surface-variant: '#45464d'
  inverse-surface: '#213145'
  inverse-on-surface: '#eaf1ff'
  outline: '#76777d'
  outline-variant: '#c6c6cd'
  surface-tint: '#565e74'
  primary: '#000000'
  on-primary: '#ffffff'
  primary-container: '#131b2e'
  on-primary-container: '#7c839b'
  inverse-primary: '#bec6e0'
  secondary: '#006d30'
  on-secondary: '#ffffff'
  secondary-container: '#92f5a4'
  on-secondary-container: '#007233'
  tertiary: '#000000'
  on-tertiary: '#ffffff'
  tertiary-container: '#001d32'
  on-tertiary-container: '#3d89c3'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#dae2fd'
  primary-fixed-dim: '#bec6e0'
  on-primary-fixed: '#131b2e'
  on-primary-fixed-variant: '#3f465c'
  secondary-fixed: '#95f8a7'
  secondary-fixed-dim: '#79db8d'
  on-secondary-fixed: '#00210a'
  on-secondary-fixed-variant: '#005323'
  tertiary-fixed: '#cde5ff'
  tertiary-fixed-dim: '#94ccff'
  on-tertiary-fixed: '#001d32'
  on-tertiary-fixed-variant: '#004b74'
  background: '#f8f9ff'
  on-background: '#0b1c30'
  surface-variant: '#d3e4fe'
typography:
  display:
    fontFamily: Manrope
    fontSize: 48px
    fontWeight: '700'
    lineHeight: '1.2'
    letterSpacing: -0.02em
  h1:
    fontFamily: Manrope
    fontSize: 32px
    fontWeight: '700'
    lineHeight: '1.25'
  h2:
    fontFamily: Manrope
    fontSize: 24px
    fontWeight: '600'
    lineHeight: '1.3'
  h3:
    fontFamily: Manrope
    fontSize: 20px
    fontWeight: '600'
    lineHeight: '1.4'
  body-lg:
    fontFamily: Manrope
    fontSize: 18px
    fontWeight: '400'
    lineHeight: '1.6'
  body-md:
    fontFamily: Manrope
    fontSize: 16px
    fontWeight: '400'
    lineHeight: '1.5'
  body-sm:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '400'
    lineHeight: '1.5'
  label-md:
    fontFamily: Manrope
    fontSize: 12px
    fontWeight: '600'
    lineHeight: '1'
    letterSpacing: 0.05em
  data-tabular:
    fontFamily: Manrope
    fontSize: 14px
    fontWeight: '500'
    lineHeight: '1.4'
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 8px
  xs: 4px
  sm: 12px
  md: 24px
  lg: 40px
  xl: 64px
  gutter: 24px
  margin: 32px
---

## Brand & Style

The design system is anchored in the principles of administrative excellence and community well-being. It targets property managers who require precision and residents who seek clarity and ease of use. The emotional response is one of "organized harmony"—reducing the friction of communal living through structured, calm, and reliable interfaces.

The visual style follows a **Corporate / Modern** aesthetic. It prioritizes clarity over decoration, using generous whitespace to separate complex data points. The interface feels lightweight and premium, avoiding the cluttered look of legacy management software in favor of a clean, SaaS-inspired experience that fosters trust.

## Colors

The palette is designed to balance authority with growth. 
- **Primary (Deep Blue):** Used for navigation, primary headings, and core actions to establish a professional foundation of trust.
- **Secondary (Professional Green):** Represents community harmony and "active" health. It is used for success states, reservation approvals, and community growth initiatives.
- **Tertiary (Azure Blue):** Utilized for information highlights and links, providing a softer alternative to the deep primary blue.
- **Neutrals:** A range of cool grays (Slate) ensures that backgrounds remain clean and borders stay unobtrusive, allowing administrative data to remain the focal point.

## Typography

This design system utilizes **Manrope** to bridge the gap between technical precision and human warmth. 
- **Headlines:** Set with tighter letter-spacing and heavier weights to command attention in administrative dashboards.
- **Body Text:** Optimized for long-form community notices and legal bylaws, prioritizing line height for readability.
- **Tabular Data:** Specifically scaled for density without sacrificing legibility, ensuring that financial reports and resident lists are easily scannable.

## Layout & Spacing

This design system employs a **12-column fluid grid** for dashboard views, transitioning to a centralized fixed-width container for reading-heavy community notices. 

The spacing rhythm is built on an **8px base unit**. Generous margins (32px) and gutters (24px) are used to prevent the "data fatigue" common in management applications. Large "empty states" and wide paddings in card components are intentional, creating a sense of calm and order.

## Elevation & Depth

Visual hierarchy is achieved through **Tonal Layers** and **Ambient Shadows**. 
- **Level 0 (Background):** The base Slate-50 surface.
- **Level 1 (Cards/Sections):** White surfaces with a subtle 1px border (#E2E8F0) and a very soft, diffused shadow (4px blur, 2% opacity) to provide a "lifted" feel from the background.
- **Level 2 (Modals/Dropdowns):** Higher contrast shadows with a 12px blur to indicate immediate priority and temporary interaction.

Navigation rails and sidebars use a subtle tonal shift (Level 0 to Level 0.5) rather than heavy shadows to maintain a sleek, modern SaaS profile.

## Shapes

The design system utilizes a **Rounded (Level 2)** shape language. 
- **Standard Components:** Buttons, input fields, and small cards use an 8px radius (0.5rem).
- **Containers:** Large dashboard modules and main content areas use a 16px radius (1rem) to soften the professional aesthetic.
- **Status Pills:** Use a fully rounded "pill" shape to distinguish them from interactive buttons.

This roundedness communicates approachability and modern harmony, contrasting with the sharp, clinical edges of traditional enterprise software.

## Components

### Buttons
Primary buttons use the Deep Blue background with white text. Secondary buttons use a transparent background with a Slate-200 border. All buttons feature an 8px corner radius and 12px x 24px padding.

### Status Indicators (Chips)
Status chips are critical for "Reservations" and "Occurrences":
- **Approved/Resolved:** Soft Green background with Deep Green text.
- **Pending/Processing:** Soft Amber background with Brown text.
- **Denied/Urgent:** Soft Red background with Dark Red text.
These use a pill-shape (fully rounded) and uppercase label typography.

### Input Fields
Inputs feature a 1px border (#E2E8F0) that transitions to Azure Blue on focus. Labels are positioned above the field using the `label-md` style for maximum clarity.

### Data Tables
Tables are the heart of the administration. They use a flat style with subtle horizontal dividers only. Row hover states use a very light Slate-50 tint to help the user track data across wide screens.

### Navigation
A persistent left-hand navigation rail uses the Primary Deep Blue or a high-contrast white. Icons are linear and 24px, paired with `body-sm` labels for a compact, intuitive sidebar.

### Cards
Cards are the primary container for "Occurrences." They include a header area for the timestamp/unit number and a footer for the status indicator, ensuring a clear information hierarchy.